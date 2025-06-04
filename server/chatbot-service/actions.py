import os
import logging
import json
import time
from typing import Any, Text, Dict, List, Optional
import requests
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.events import SlotSet

from dotenv import load_dotenv
load_dotenv()

# Configure logger first
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

EUREKA_SERVER_URL = os.getenv('EUREKA_SERVER_URL')
INTERNAL_API_KEY = os.getenv('INTERNAL_API_KEY')

# Add validation for required environment variables
if not EUREKA_SERVER_URL:
    logger.warning("EUREKA_SERVER_URL not set in environment variables, using localhost fallback")
    EUREKA_SERVER_URL = "http://localhost:8761/eureka"
if not INTERNAL_API_KEY:
    logger.warning("INTERNAL_API_KEY not set in environment variables")

class EurekaServiceDiscovery:
    
    
    def __init__(self):
        self.eureka_url = EUREKA_SERVER_URL
        self._service_cache = {}
        self._cache_expiry = {}
        self.cache_ttl = 30  
    
    def get_service_url(self, service_name: str) -> Optional[str]:
        
        current_time = time.time()
        
        # Check if EUREKA_SERVER_URL is available
        if not self.eureka_url:
            logger.error("EUREKA_SERVER_URL is not configured")
            return None
        
        # Check cache first
        if (service_name in self._service_cache and 
            current_time < self._cache_expiry.get(service_name, 0)):
            logger.info(f"Using cached service URL for {service_name}: {self._service_cache[service_name]}")
            return self._service_cache[service_name]
        
        try:
            
            eureka_apps_url = f"{self.eureka_url}/apps/{service_name.upper()}"
            logger.info(f"Requesting Eureka URL: {eureka_apps_url}")
            response = requests.get(eureka_apps_url, 
                                  headers={'Accept': 'application/json'},
                                  timeout=5)
            
            logger.info(f"Eureka response status: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                application = data.get('application', {})
                instances = application.get('instance', [])
                
                logger.info(f"Found {len(instances)} instances for {service_name}")
                
                if instances:
                    
                    for instance in instances:
                        if instance.get('status') == 'UP':
                            ip = instance.get('ipAddr')
                            port = instance.get('port', {}).get('$', 80)
                            service_url = f"http://{ip}:{port}"
                            
                            
                            self._service_cache[service_name] = service_url
                            self._cache_expiry[service_name] = current_time + self.cache_ttl
                            
                            logger.info(f"Found service {service_name} at {service_url}")
                            return service_url
                    
                    logger.warning(f"Found instances for {service_name} but none are UP")
                else:
                    logger.warning(f"No instances found for service: {service_name}")
            else:
                logger.error(f"Eureka request failed with status {response.status_code}: {response.text}")
                            
            logger.warning(f"No healthy instance found for service: {service_name}")
            return None
            
        except Exception as e:
            logger.error(f"Error getting service URL for {service_name}: {e}")
            return None

class CatalogServiceClient:
    
    
    def __init__(self):
        self.eureka_client = EurekaServiceDiscovery()
        self.service_name = "catalog-service"
        self.internal_api_key = INTERNAL_API_KEY
        # Add fallback URL for development/testing - USE LOCALHOST WHEN RUNNING OUTSIDE DOCKER
        self.fallback_url = os.getenv('CATALOG_SERVICE_FALLBACK_URL', 'http://localhost:8089')
        
        # If Eureka URL contains localhost or we can't resolve eureka-server, assume we're outside Docker
        self.use_direct_url = True
        if EUREKA_SERVER_URL and 'eureka-server' in EUREKA_SERVER_URL:
            try:
                # Test if we can resolve eureka-server hostname
                import socket
                socket.gethostbyname('eureka-server')
                self.use_direct_url = False  # We're inside Docker network
                logger.info("Running inside Docker network, will use Eureka service discovery")
            except socket.gaierror:
                self.use_direct_url = True  # We're outside Docker network
                logger.info("Running outside Docker network, will use direct localhost URLs")
        
        logger.info(f"CatalogServiceClient initialized - use_direct_url: {self.use_direct_url}, fallback_url: {self.fallback_url}")
    
    def _get_headers(self) -> Dict[str, str]:
        
        headers = {
            'Content-Type': 'application/json'
        }
        if self.internal_api_key:
            headers['X-Internal-Api-Key'] = self.internal_api_key
        return headers
    
    def _get_service_url(self) -> Optional[str]:
        """Get service URL with fallback mechanism"""
        # If we're outside Docker, use direct URL immediately
        if self.use_direct_url:
            logger.info(f"Using direct URL: {self.fallback_url}")
            return self.fallback_url
            
        # Otherwise, try Eureka discovery first
        service_url = self.eureka_client.get_service_url(self.service_name)
        if not service_url and self.fallback_url:
            logger.info(f"Eureka discovery failed, using fallback URL: {self.fallback_url}")
            return self.fallback_url
        return service_url
    
    def _make_request(self, endpoint: str, params: Optional[Dict] = None) -> Optional[Dict]:
        
        service_url = self._get_service_url()
        if not service_url:
            logger.error(f"Cannot find {self.service_name} in Eureka registry and no fallback URL configured")
            # Return mock data for demonstration when service is not available
            return self._get_mock_data(endpoint)
        
        try:
            url = f"{service_url}{endpoint}"
            logger.info(f"Making request to: {url} with params: {params}")
            
            response = requests.get(
                url, 
                params=params, 
                headers=self._get_headers(),
                timeout=10
            )
            
            logger.info(f"Response status: {response.status_code}")
            
            if response.status_code == 200:
                try:
                    return response.json()
                except json.JSONDecodeError as e:
                    logger.error(f"Failed to parse JSON response: {e}")
                    logger.error(f"Response content: {response.text[:500]}")
                    return self._get_mock_data(endpoint)
            else:
                logger.error(f"API call failed: {response.status_code} - {response.text}")
                return self._get_mock_data(endpoint)
                
        except requests.exceptions.Timeout as e:
            logger.error(f"Timeout error calling {endpoint}: {e}")
            return self._get_mock_data(endpoint)
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Connection error calling {endpoint}: {e}")
            return self._get_mock_data(endpoint)
        except requests.exceptions.RequestException as e:
            logger.error(f"Network error calling {endpoint}: {e}")
            return self._get_mock_data(endpoint)
        except Exception as e:
            logger.error(f"Unexpected error calling {endpoint}: {e}")
            return self._get_mock_data(endpoint)
    
    def _get_mock_data(self, endpoint: str) -> Optional[Dict]:
        """Provide mock data when service is unavailable"""
        if '/products' in endpoint:
            return {
                'content': [
                    {
                        'id': 1,
                        'name': 'Hoa Hồng Đỏ Classic',
                        'price': 350000,
                        'description': 'Bó hoa hồng đỏ tươi đẹp, thích hợp cho valentine và các dịp đặc biệt'
                    },
                    {
                        'id': 2,
                        'name': 'Hoa Tulip Vàng Spring',
                        'price': 280000,
                        'description': 'Bó hoa tulip vàng tươi mới, mang lại cảm giác ấm áp và vui tươi'
                    },
                    {
                        'id': 3,
                        'name': 'Hoa Lan Trắng Elegant',
                        'price': 450000,
                        'description': 'Chậu hoa lan trắng thanh lịch, phù hợp cho trang trí và làm quà'
                    },
                    {
                        'id': 4,
                        'name': 'Hoa Cẩm Chướng Hồng Sweet',
                        'price': 220000,
                        'description': 'Bó hoa cẩm chướng hồng ngọt ngào, thể hiện tình cảm chân thành'
                    },
                    {
                        'id': 5,
                        'name': 'Hoa Baby Mix Pastel',
                        'price': 180000,
                        'description': 'Bó hoa baby nhiều màu pastel nhẹ nhàng, rất đáng yêu'
                    }
                ]
            }
        elif '/categories' in endpoint:
            return [
                {'id': 1, 'name': 'Roses', 'description': 'Hoa hồng các loại'},
                {'id': 2, 'name': 'Tulips', 'description': 'Hoa tulip nhập khẩu'},
                {'id': 3, 'name': 'Orchids', 'description': 'Hoa lan cao cấp'},
                {'id': 4, 'name': 'Lilies', 'description': 'Hoa ly thuần khiết'},
                {'id': 5, 'name': 'Mixed Bouquets', 'description': 'Bó hoa kết hợp'}
            ]
        return None
    
    def get_products(self, name: str = None, min_price: float = None, 
                    max_price: float = None, category_id: int = None, 
                    size: int = 10) -> Optional[Dict]:
        
        params = {'size': size}
        if name:
            params['name'] = name
        if min_price:
            params['minPrice'] = min_price
        if max_price:
            params['maxPrice'] = max_price
        if category_id:
            params['categoryId'] = category_id
            
        return self._make_request('/api/v1/internal/products', params)
    
    def get_all_products(self) -> Optional[List[Dict]]:
        
        return self._make_request('/api/v1/internal/products/all')
    
    def get_product_by_id(self, product_id: int) -> Optional[Dict]:
        
        return self._make_request(f'/api/v1/internal/products/{product_id}')
    
    def get_categories(self) -> Optional[List[Dict]]:
        
        return self._make_request('/categories')
    
    def get_products_by_category(self, category_id: int, size: int = 10) -> Optional[Dict]:
        
        params = {'size': size}
        logger.info(f"Getting products for category_id: {category_id} with size: {size}")
        return self._make_request(f'/api/v1/internal/products/category/{category_id}', params)



catalog_client = CatalogServiceClient()

class ActionFlowerAdvice(Action):
    
    
    def name(self) -> Text:
        return "action_flower_advice"

    def _create_recommendation_message(self, products: List[Dict], occasion: str, color: str, flower_type: str) -> str:
        """
        Create a formatted recommendation message
        """
        context_desc = []
        if flower_type:
            context_desc.append(flower_type)
        if color:
            context_desc.append(f"màu {color}")
        if occasion:
            context_desc.append(f"cho {occasion}")
            
        intro = f"Dựa trên yêu cầu {' '.join(context_desc) if context_desc else 'của bạn'}, shop gợi ý:\n\n"
        
        recommendations = []
        for i, product in enumerate(products, 1):
            name = product.get('name', 'Sản phẩm')
            price = product.get('price', 0)
            description = product.get('description', '')
            
            price_text = f"{int(price):,}".replace(',', '.')
            rec_text = f"{i}. **{name}** - {price_text} VNĐ"
            if description:
                rec_text += f"\n   {description[:100]}{'...' if len(description) > 100 else ''}"
            recommendations.append(rec_text)
        
        footer = "\n\nBạn muốn biết thêm chi tiết về sản phẩm nào không?"
        
        return intro + "\n\n".join(recommendations) + footer

    def _should_clear_previous_context(self, current_occasion: str, previous_occasion: str) -> bool:
        """
        Determine if we should clear previous context based on occasion change
        """
        if not previous_occasion or not current_occasion:
            return False
            
        
        different_occasions = {
            'sinh nhật': ['valentine', 'đám cưới', 'khai trương'],
            'valentine': ['sinh nhật', 'đám cưới', 'khai trương'], 
            'đám cưới': ['sinh nhật', 'valentine', 'khai trương'],
        }
        
        return current_occasion in different_occasions.get(previous_occasion, [])

    def _detect_occasion_from_flower_type(self, flower_type: str) -> str:
        """
        Detect occasion from flower type like "hoa valentine"
        """
        if not flower_type:
            return None
            
        flower_type_lower = flower_type.lower()
        
        if any(keyword in flower_type_lower for keyword in ['valentine', 'lễ tình nhân', '14/2']):
            return 'valentine'
        elif any(keyword in flower_type_lower for keyword in ['sinh nhật', 'birthday']):
            return 'sinh nhật'
        elif any(keyword in flower_type_lower for keyword in ['đám cưới', 'wedding']):
            return 'đám cưới'
        elif any(keyword in flower_type_lower for keyword in ['khai trương', 'opening']):
            return 'khai trương'
            
        return None

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        
        flower_type = next(tracker.get_latest_entity_values("flower_type"), None)
        color = next(tracker.get_latest_entity_values("color"), None)
        occasion = next(tracker.get_latest_entity_values("occasion"), None)
        budget = next(tracker.get_latest_entity_values("budget"), None)

        
        prev_flower_type = tracker.get_slot("flower_type")
        prev_color = tracker.get_slot("color")
        prev_occasion = tracker.get_slot("occasion")
        prev_budget = tracker.get_slot("budget")

        
        if not occasion and flower_type:
            detected_occasion = self._detect_occasion_from_flower_type(flower_type)
            if detected_occasion:
                occasion = detected_occasion

        
        flower_type = flower_type or prev_flower_type
        color = color or prev_color
        budget = budget or prev_budget
        
        
        if occasion and prev_occasion and occasion != prev_occasion:
            
            logger.info(f"Context switch detected: {prev_occasion} -> {occasion}")
            flower_type = next(tracker.get_latest_entity_values("flower_type"), None) or flower_type
            color = next(tracker.get_latest_entity_values("color"), None) or color
        else:
            occasion = occasion or prev_occasion

        logger.info(f"Flower advice request - Type: {flower_type}, Color: {color}, Occasion: {occasion}, Budget: {budget}")

        try:
            
            budget_value = None
            if budget:
                budget_value = self._parse_budget(budget)

            
            search_params = {
                'size': 5
            }
            
            
            if flower_type:
                search_params['name'] = flower_type

            
            if budget_value:
                search_params['max_price'] = budget_value

            
            data = catalog_client.get_products(**search_params)
            
            if data:
                products = data.get('content', []) if isinstance(data, dict) else data
                
                if products and len(products) > 0:
                    
                    filtered_products = self._filter_products(products, color, occasion, flower_type)
                    
                    if filtered_products:
                        message = self._create_recommendation_message(filtered_products, occasion, color, flower_type)
                    else:
                        
                        criteria = []
                        if flower_type: criteria.append(flower_type)
                        if color: criteria.append(f"màu {color}")
                        if occasion: criteria.append(f"phù hợp với {occasion}")
                        
                        criteria_text = " ".join(criteria) if criteria else "theo yêu cầu của bạn"
                        message = f"Rất tiếc, hiện tại shop chưa có hoa {criteria_text}."
                        
                        
                        if len(products) > 0:
                            message += " Tuy nhiên, shop có một số gợi ý khác:\n\n"
                            for i, product in enumerate(products[:3], 1):
                                name = product.get('name', 'Sản phẩm')
                                price = product.get('price', 0)
                                price_text = f"{int(price):,}".replace(',', '.')
                                message += f"{i}. **{name}** - {price_text} VNĐ\n"
                else:
                    message = "Hiện tại shop chưa có sản phẩm phù hợp. Bạn có thể thử tìm với tiêu chí khác không?"
            else:
                message = "Xin lỗi, không thể kết nối tới hệ thống. Vui lòng thử lại sau!"

        except Exception as e:
            logger.error(f"Unexpected error in flower advice: {e}")
            message = "Có lỗi xảy ra. Vui lòng thử lại sau!"

        dispatcher.utter_message(text=message)
        
        
        return [
            SlotSet("flower_type", flower_type),
            SlotSet("color", color),
            SlotSet("occasion", occasion),
            SlotSet("budget", budget)
        ]

    def _parse_budget(self, budget_text: str) -> float:
        
        if not budget_text:
            return None
            
        try:
            budget_text = str(budget_text).lower()
            if 'k' in budget_text:
                return float(budget_text.replace('k', '')) * 1000
            elif 'triệu' in budget_text:
                return float(budget_text.replace('triệu', '').strip()) * 1000000
            elif 'nghìn' in budget_text:
                return float(budget_text.replace('nghìn', '').strip()) * 1000
            else:
                return float(budget_text)
        except:
            return None

    def _filter_products(self, products: List[Dict], color: str, occasion: str, flower_type: str) -> List[Dict]:
        
        filtered = products.copy()
        
        
        if color:
            filtered = [p for p in filtered if color.lower() in (p.get('name', '') + ' ' + p.get('description', '')).lower()]
        
        
        if flower_type:
            filtered = [p for p in filtered if flower_type.lower() in p.get('name', '').lower()]
            
        return filtered[:3]  


class ActionGetProducts(Action):
    
    
    def name(self) -> Text:
        return "action_get_products"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        try:
            products = catalog_client.get_all_products()
            
            if products:
                message = "Danh sách các loại hoa hiện có:\n\n"
                for i, product in enumerate(products[:10], 1):  
                    name = product.get('name', 'Sản phẩm')
                    price = product.get('price', 0)
                    price_text = f"{int(price):,}".replace(',', '.')
                    message += f"{i}. {name} - {price_text} VNĐ\n"
                message += "\nBạn quan tâm đến loại hoa nào?"
            else:
                message = "Hiện tại shop chưa có sản phẩm nào hoặc không thể kết nối hệ thống."

        except Exception as e:
            logger.error(f"Error getting products: {e}")
            message = "Có lỗi xảy ra khi lấy danh sách sản phẩm!"

        dispatcher.utter_message(text=message)
        return []


class ActionGetCategories(Action):
    
    
    def name(self) -> Text:
        return "action_get_categories"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        try:
            categories = catalog_client.get_categories()
            
            if categories:
                message = "Các danh mục hoa tại shop:\n\n"
                for i, category in enumerate(categories, 1):
                    name = category.get('name', 'Danh mục')
                    description = category.get('description', '')
                    message += f"{i}. **{name}**"
                    if description:
                        message += f" - {description}"
                    message += "\n"
                message += "\nBạn muốn xem hoa ở danh mục nào?"
            else:
                message = "Hiện tại chưa có danh mục nào hoặc không thể kết nối hệ thống."

        except Exception as e:
            logger.error(f"Error getting categories: {e}")
            message = "Có lỗi xảy ra khi lấy danh mục!"

        dispatcher.utter_message(text=message)
        return []


class ActionGetProductDetails(Action):
    
    
    def name(self) -> Text:
        return "action_get_product_details"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        
        product_id = next(tracker.get_latest_entity_values("product_id"), None)
        
        if not product_id:
            message = "Bạn muốn xem chi tiết sản phẩm nào? Vui lòng cho tôi biết tên hoặc mã sản phẩm."
            dispatcher.utter_message(text=message)
            return []

        try:
            product = catalog_client.get_product_by_id(int(product_id))
            
            if product:
                name = product.get('name', 'Sản phẩm')
                description = product.get('description', 'Không có mô tả')
                price = product.get('price', 0)
                price_text = f"{int(price):,}".replace(',', '.')
                
                message = f"**{name}**\n\n"
                message += f"💰 Giá: {price_text} VNĐ\n"
                message += f"📝 Mô tả: {description}\n\n"
                message += "Bạn có muốn đặt hàng không?"
            else:
                message = "Không tìm thấy sản phẩm này hoặc không thể kết nối hệ thống."

        except Exception as e:
            logger.error(f"Error getting product details: {e}")
            message = "Có lỗi xảy ra khi lấy thông tin sản phẩm!"

        dispatcher.utter_message(text=message)
        return []


class ActionGetPriceRange(Action):
    
    
    def name(self) -> Text:
        return "action_get_price_range"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        flower_type = next(tracker.get_latest_entity_values("flower_type"), None)

        try:
            data = catalog_client.get_products(name=flower_type, size=100)
            
            if data:
                products = data.get('content', []) if isinstance(data, dict) else data
                
                if products:
                    prices = []
                    for p in products:
                        price_str = p.get('price', '0')
                        try:
                            price_float = float(price_str)
                            prices.append(price_float)
                        except (ValueError, TypeError):
                            logger.warning(f"Invalid price format: {price_str}")
                            continue
                    
                    if prices:
                        min_price = min(prices)
                        max_price = max(prices)
                        avg_price = sum(prices) / len(prices)
                        
                        min_text = f"{int(min_price):,}".replace(',', '.')
                        max_text = f"{int(max_price):,}".replace(',', '.')
                        avg_text = f"{int(avg_price):,}".replace(',', '.')
                        
                        if flower_type:
                            message = f"Thông tin giá {flower_type}:\n\n"
                        else:
                            message = "Thông tin giá các loại hoa:\n\n"
                        
                        message += f"💰 Giá thấp nhất: {min_text} VNĐ\n"
                        message += f"💰 Giá cao nhất: {max_text} VNĐ\n"
                        message += f"💰 Giá trung bình: {avg_text} VNĐ\n\n"
                        message += "Bạn muốn xem sản phẩm nào cụ thể?"
                    else:
                        message = "Hiện tại chưa có thông tin giá hợp lệ cho sản phẩm này."
                else:
                    message = "Hiện tại chưa có thông tin giá sản phẩm."
            else:
                message = "Không thể lấy thông tin giá. Vui lòng thử lại sau!"

        except Exception as e:
            logger.error(f"Error getting price range: {e}")
            message = "Có lỗi xảy ra khi lấy thông tin giá!"

        dispatcher.utter_message(text=message)
        return []


class ActionRecommendFlowers(Action):
    
    
    def name(self) -> Text:
        return "action_recommend_flowers"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        try:
            
            data = catalog_client.get_products(size=5)
            
            if data:
                products = data.get('content', []) if isinstance(data, dict) else data
                
                if products:
                    message = "🌟 Top hoa được yêu thích nhất:\n\n"
                    for i, product in enumerate(products[:5], 1):
                        name = product.get('name', 'Sản phẩm')
                        price = product.get('price', 0)
                        price_text = f"{int(price):,}".replace(',', '.')
                        message += f"{i}. **{name}** - {price_text} VNĐ\n"
                    
                    message += "\nBạn muốn biết thêm về sản phẩm nào không?"
                else:
                    message = "Hiện tại chưa có đủ dữ liệu để gợi ý."
            else:
                message = "Không thể lấy danh sách gợi ý. Vui lòng thử lại sau!"

        except Exception as e:
            logger.error(f"Error getting recommendations: {e}")
            message = "Có lỗi xảy ra khi lấy gợi ý!"

        dispatcher.utter_message(text=message)
        return []


class ActionGetProductsByCategory(Action):
    """
    Action to handle category selection and show products in specific category
    """
    
    def name(self) -> Text:
        return "action_get_products_by_category"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        category_name = next(tracker.get_latest_entity_values("category_name"), None)
        
        if not category_name:
            message = "Bạn muốn xem danh mục nào? Vui lòng chọn từ danh sách categories đã hiển thị."
            dispatcher.utter_message(text=message)
            return []

        logger.info(f"Category selection request - Category: {category_name}")

        try:
            category_mapping = {
                'Anemones': 1,
                'Dried Flowers': 2,
                'Hydrangeas': 3,
                'Lilies': 4,
                'Orchids': 5,
                'Peonies': 6,
                'Ranunculus': 7,
                'Roses': 8,
                'Succulents': 9,
                'Sunflowers': 10,
                'Tropical': 11,
                'Tulips': 12,
                'Unique Stems': 13
            }
            
            category_id = category_mapping.get(category_name)
            
            if not category_id:
                logger.warning(f"Category '{category_name}' not found in mapping")
                message = f"Không tìm thấy danh mục '{category_name}'. Vui lòng chọn từ danh sách categories có sẵn."
                dispatcher.utter_message(text=message)
                return []

            logger.info(f"Mapped category '{category_name}' to ID: {category_id}")
            
            # Primary attempt: get products by category
            data = catalog_client.get_products_by_category(category_id, size=10)
            
            if data:
                products = data.get('content', []) if isinstance(data, dict) else data
                logger.info(f"Retrieved {len(products) if products else 0} products for category {category_name}")
                
                if products and len(products) > 0:
                    message = f"🌸 **{category_name}** có tại shop:\n\n"
                    
                    for i, product in enumerate(products, 1):
                        name = product.get('name', 'Sản phẩm')
                        price = product.get('price', 0)
                        description = product.get('description_text', '') or product.get('description', '')
                        
                        try:
                            price_text = f"{int(float(price)):,}".replace(',', '.')
                        except (ValueError, TypeError):
                            price_text = "Liên hệ"
                            logger.warning(f"Invalid price format for product {name}: {price}")
                        
                        message += f"{i}. **{name}** - {price_text} VNĐ\n"
                        if description:
                            message += f"   {description[:80]}{'...' if len(description) > 80 else ''}\n"
                        message += "\n"
                    
                    message += "Bạn muốn biết thêm chi tiết về sản phẩm nào không?"
                else:
                    logger.info(f"No products found for category {category_name}")
                    # Fallback: try to get general products and filter by name
                    logger.info("Attempting fallback search by category name")
                    fallback_data = catalog_client.get_products(name=category_name.lower(), size=5)
                    
                    if fallback_data:
                        fallback_products = fallback_data.get('content', []) if isinstance(fallback_data, dict) else fallback_data
                        if fallback_products and len(fallback_products) > 0:
                            message = f"🌸 Một số sản phẩm liên quan đến **{category_name}**:\n\n"
                            for i, product in enumerate(fallback_products, 1):
                                name = product.get('name', 'Sản phẩm')
                                price = product.get('price', 0)
                                try:
                                    price_text = f"{int(float(price)):,}".replace(',', '.')
                                except (ValueError, TypeError):
                                    price_text = "Liên hệ"
                                message += f"{i}. **{name}** - {price_text} VNĐ\n"
                            message += "\nBạn muốn biết thêm chi tiết về sản phẩm nào không?"
                        else:
                            message = f"Rất tiếc, hiện tại danh mục {category_name} chưa có sản phẩm nào."
                    else:
                        message = f"Rất tiếc, hiện tại danh mục {category_name} chưa có sản phẩm nào."
            else:
                logger.error(f"Failed to get data for category {category_name} (ID: {category_id})")
                # Fallback: try to get general products
                logger.info("Attempting fallback to general product search")
                fallback_data = catalog_client.get_products(size=5)
                
                if fallback_data:
                    fallback_products = fallback_data.get('content', []) if isinstance(fallback_data, dict) else fallback_data
                    if fallback_products and len(fallback_products) > 0:
                        message = f"Hiện tại không thể lấy danh mục {category_name}, nhưng đây là một số sản phẩm khác:\n\n"
                        for i, product in enumerate(fallback_products[:3], 1):
                            name = product.get('name', 'Sản phẩm')
                            price = product.get('price', 0)
                            try:
                                price_text = f"{int(float(price)):,}".replace(',', '.')
                            except (ValueError, TypeError):
                                price_text = "Liên hệ"
                            message += f"{i}. **{name}** - {price_text} VNĐ\n"
                        message += "\nVui lòng thử lại sau để xem danh mục đầy đủ."
                    else:
                        message = "Không thể kết nối tới hệ thống. Vui lòng thử lại sau!"
                else:
                    message = "Không thể kết nối tới hệ thống. Vui lòng thử lại sau!"

        except Exception as e:
            logger.error(f"Error in category selection: {e}", exc_info=True)
            message = "Có lỗi xảy ra khi lấy sản phẩm theo danh mục. Vui lòng thử lại sau!"

        dispatcher.utter_message(text=message)
        
        
        return [
            SlotSet("category_name", category_name)
        ]


class ActionSpecificFlowerSearch(Action):
    """
    Action to handle specific flower search requests like "rose", "hoa hồng", etc.
    """
    
    def name(self) -> Text:
        return "action_specific_flower_search"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        
        flower_type = next(tracker.get_latest_entity_values("flower_type"), None)
        if not flower_type:
            flower_type = tracker.get_slot("flower_type")

        logger.info(f"Specific flower search request - Type: {flower_type}")

        if not flower_type:
            message = "Bạn muốn tìm loại hoa nào ạ? Ví dụ: hoa hồng, hoa lan, hoa tulip..."
            dispatcher.utter_message(text=message)
            return []

        try:
            
            data = catalog_client.get_products(name=flower_type, size=5)
            
            if data:
                products = data.get('content', []) if isinstance(data, dict) else data
                logger.info(f"Retrieved {len(products) if products else 0} products for flower type {flower_type}")
                
                if products and len(products) > 0:
                    
                    message = f"🌸 **{flower_type.title()}** có tại shop:\n\n"
                    
                    for i, product in enumerate(products, 1):
                        name = product.get('name', 'Sản phẩm')
                        price = product.get('price', 0)
                        description = product.get('description', '')
                        
                        try:
                            price_text = f"{int(float(price)):,}".replace(',', '.')
                        except (ValueError, TypeError):
                            price_text = "Liên hệ"
                            logger.warning(f"Invalid price format for product {name}: {price}")
                        
                        message += f"{i}. **{name}** - {price_text} VNĐ\n"
                        if description:
                            message += f"   {description[:80]}{'...' if len(description) > 80 else ''}\n"
                        message += "\n"
                    
                    message += "Bạn muốn biết thêm chi tiết về sản phẩm nào không?"
                else:
                    logger.info(f"No products found for flower type {flower_type}")
                    # Try fallback with general product search
                    logger.info("Attempting fallback search with general products")
                    fallback_data = catalog_client.get_products(size=5)
                    
                    if fallback_data:
                        fallback_products = fallback_data.get('content', []) if isinstance(fallback_data, dict) else fallback_data
                        if fallback_products and len(fallback_products) > 0:
                            message = f"Rất tiếc, hiện tại shop chưa có {flower_type} trong kho. Tuy nhiên, đây là một số loại hoa khác:\n\n"
                            for i, product in enumerate(fallback_products[:3], 1):
                                name = product.get('name', 'Sản phẩm')
                                price = product.get('price', 0)
                                try:
                                    price_text = f"{int(float(price)):,}".replace(',', '.')
                                except (ValueError, TypeError):
                                    price_text = "Liên hệ"
                                message += f"{i}. **{name}** - {price_text} VNĐ\n"
                            message += "\nBạn có muốn xem loại nào khác không?"
                        else:
                            message = f"Rất tiếc, hiện tại shop chưa có {flower_type} trong kho. Bạn có muốn xem các loại hoa khác không?"
                    else:
                        message = f"Rất tiếc, hiện tại shop chưa có {flower_type} trong kho. Bạn có muốn xem các loại hoa khác không?"
            else:
                logger.error(f"Failed to get data for flower type {flower_type}")
                message = "Không thể kết nối tới hệ thống. Vui lòng thử lại sau!"

        except Exception as e:
            logger.error(f"Error in specific flower search: {e}", exc_info=True)
            message = "Có lỗi xảy ra khi tìm kiếm. Vui lòng thử lại sau!"

        dispatcher.utter_message(text=message)
        
        
        return [
            SlotSet("flower_type", flower_type),
            SlotSet("occasion", None),  
            SlotSet("color", None),     
            SlotSet("budget", None)     
        ]


class ActionHealthCheck(Action):
    """
    Action to handle health check requests
    """
    
    def name(self) -> Text:
        return "action_health_check"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        try:
            # Test catalog service connection
            data = catalog_client.get_products(size=1)
            
            if data:
                message = "✅ Hệ thống đang hoạt động bình thường!\n"
                message += "📊 Catalog service: Kết nối thành công\n"
                message += "🤖 Chatbot: Hoạt động tốt\n"
                message += "Bạn có thể hỏi tôi về các loại hoa nhé!"
            else:
                message = "⚠️ Hệ thống đang gặp một số vấn đề nhỏ.\n"
                message += "Tuy nhiên, tôi vẫn có thể hỗ trợ bạn tư vấn chung về hoa!"
                
        except Exception as e:
            logger.error(f"Health check failed: {e}")
            message = "⚠️ Đang kiểm tra hệ thống...\n"
            message += "Tôi vẫn có thể hỗ trợ bạn tư vấn về hoa!"

        dispatcher.utter_message(text=message)
        return []


        
        
        

