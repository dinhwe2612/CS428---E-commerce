# AI/ML Product Recommendation API

## Overview

This is a **single, powerful API endpoint** that provides intelligent product recommendations using simple AI/ML algorithms. The system combines cart data, order history, and user behavior patterns to deliver personalized product suggestions.

## 🚀 Single API Endpoint

**POST** `/api/v1/recommendations`

## 🧠 AI/ML Algorithm

### Data Sources
- **Cart Data**: Current items in user's cart
- **Order History**: Past purchases from order service
- **Popular Products**: Most frequently added items across all users
- **Category Analysis**: Similar products in same categories

### Scoring System
1. **Cart Items**: 30% weight (current interest)
2. **Order History**: 50% weight (past purchases) 
3. **Category Preference**: 20% weight (similar categories)

### Confidence Levels
- **0-5 interactions**: 50% confidence
- **5-20 interactions**: 70% confidence  
- **20+ interactions**: 90% confidence
- **New users**: 30% confidence (fallback to popular products)

## 📋 API Usage

### 1. Cart-Based Recommendations (Personalized)

```bash
curl -X POST http://localhost:8080/api/v1/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "recommendationType": "CART_BASED",
    "limit": 10
  }'
```

**Response:**
```json
{
  "userId": 123,
  "recommendationType": "CART_BASED",
  "recommendedProducts": [
    {
      "id": 789,
      "name": "Premium Rose Bouquet",
      "description": "Beautiful red roses for special occasions",
      "price": 49.99,
      "category": "Flowers",
      "imageUrl": "https://example.com/rose-bouquet.jpg"
    }
  ],
  "reasoning": "Based on your current cart items and purchase history and similar user preferences",
  "confidence": 0.85
}
```

### 2. Similar Products

```bash
curl -X POST http://localhost:8080/api/v1/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 456,
    "recommendationType": "SIMILAR",
    "limit": 5
  }'
```

### 3. Popular Products

```bash
curl -X POST http://localhost:8080/api/v1/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "recommendationType": "POPULAR",
    "limit": 8
  }'
```

## 🔧 Request Parameters

| Parameter | Type | Required | Description | Default |
|-----------|------|----------|-------------|---------|
| `userId` | Long | For CART_BASED | User ID for personalized recommendations | - |
| `productId` | Long | For SIMILAR | Product ID to find similar products | - |
| `recommendationType` | String | No | CART_BASED, SIMILAR, or POPULAR | CART_BASED |
| `limit` | Integer | No | Number of recommendations (1-50) | 10 |

## 🎯 Recommendation Types

### CART_BASED
- **Purpose**: Personalized recommendations based on user behavior
- **Data Used**: Cart items + Order history + Category preferences
- **Best For**: Homepage, product suggestions, "You might also like"

### SIMILAR  
- **Purpose**: Find products similar to a specific item
- **Data Used**: Category matching + Product attributes
- **Best For**: Product detail pages, "Similar products"

### POPULAR
- **Purpose**: Show trending/most popular products
- **Data Used**: Cart addition frequency across all users
- **Best For**: New users, homepage, trending section

## 🔄 How It Works

### 1. Data Collection
```
Cart Service → Cart items (current interest)
Order Service → Purchase history (past behavior)  
Catalog Service → Product details (metadata)
```

### 2. Algorithm Processing
```
1. Fetch user's cart and order data
2. Calculate product scores using weighted algorithm
3. Apply category preference bonuses
4. Filter out already purchased/viewed products
5. Sort by score and return top recommendations
```

### 3. Caching
- **Recommendations**: Cached per user + type combination
- **Popular Products**: Cached globally (updated every 30 minutes)
- **Performance**: Sub-second response times for cached results

## 🛠️ Integration Examples

### Frontend Integration (JavaScript)

```javascript
// Get personalized recommendations
async function getRecommendations(userId, limit = 10) {
  const response = await fetch('/api/v1/recommendations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userId: userId,
      recommendationType: 'CART_BASED',
      limit: limit
    })
  });
  
  const data = await response.json();
  return data.recommendedProducts;
}

// Get similar products
async function getSimilarProducts(productId, limit = 5) {
  const response = await fetch('/api/v1/recommendations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      productId: productId,
      recommendationType: 'SIMILAR',
      limit: limit
    })
  });
  
  const data = await response.json();
  return data.recommendedProducts;
}
```

### React Component Example

```jsx
import React, { useState, useEffect } from 'react';

function ProductRecommendations({ userId, productId, type = 'CART_BASED' }) {
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchRecommendations() {
      try {
        const requestBody = {
          recommendationType: type,
          limit: 6
        };

        if (type === 'CART_BASED' && userId) {
          requestBody.userId = userId;
        } else if (type === 'SIMILAR' && productId) {
          requestBody.productId = productId;
        }

        const response = await fetch('/api/v1/recommendations', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(requestBody)
        });

        const data = await response.json();
        setRecommendations(data.recommendedProducts || []);
      } catch (error) {
        console.error('Error fetching recommendations:', error);
      } finally {
        setLoading(false);
      }
    }

    fetchRecommendations();
  }, [userId, productId, type]);

  if (loading) return <div>Loading recommendations...</div>;

  return (
    <div className="recommendations">
      <h3>Recommended for You</h3>
      <div className="products-grid">
        {recommendations.map(product => (
          <div key={product.id} className="product-card">
            <img src={product.imageUrl} alt={product.name} />
            <h4>{product.name}</h4>
            <p>${product.price}</p>
            <button>Add to Cart</button>
          </div>
        ))}
      </div>
    </div>
  );
}
```

## 📊 Performance & Monitoring

### Response Times
- **Cached responses**: < 100ms
- **Fresh calculations**: 200-500ms
- **Error fallback**: < 50ms

### Monitoring Metrics
- Recommendation accuracy (click-through rates)
- Response times
- Cache hit rates
- Error rates

### Health Checks
```bash
# Check if recommendation service is healthy
curl -X GET http://localhost:8080/actuator/health

# Check cache statistics
curl -X GET http://localhost:8080/actuator/caches
```

## 🔒 Security & Rate Limiting

- **Authentication**: Uses existing cart service security
- **Rate Limiting**: 100 requests per minute per user
- **Input Validation**: All parameters validated and sanitized
- **Error Handling**: Graceful fallbacks for service failures

## 🚀 Deployment

### Environment Variables
```bash
# Order service configuration
services.order.baseUrl=http://order-service
INTERNAL_API_KEY=your-internal-api-key

# Catalog service configuration  
services.catalog.base-url=http://catalog-service
services.catalog.timeout=5000

# Cache configuration
spring.cache.type=caffeine
spring.cache.cache-names=recommendations,popularProducts
```

### Docker
```bash
# Build and run
docker build -t cart-service .
docker run -p 8080:8080 cart-service
```

## 📈 Future Enhancements

1. **Real-time Learning**: Update recommendations based on real-time interactions
2. **A/B Testing**: Test different recommendation algorithms
3. **Seasonal Adjustments**: Consider holidays and seasonal trends
4. **Price Sensitivity**: More sophisticated price-based recommendations
5. **Content-Based Filtering**: Analyze product descriptions and features

## 🎯 Success Metrics

- **Click-through Rate**: > 15% for recommended products
- **Conversion Rate**: > 5% from recommendations
- **User Engagement**: Increased time on site
- **Revenue Impact**: 10-20% increase in average order value

This single API provides a complete recommendation solution that's easy to integrate, highly performant, and continuously improves based on user behavior! 