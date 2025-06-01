import os
import json
from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeoutError

class CategoryCrawler:
    categories = [
        ("Anemones",      "/anemones"),
        ("Dried Flowers", "/dried"),
        ("Hydrangeas",    "/hydrangeas"),
        ("Lilies",        "/lilies"),
        ("Orchids",       "/orchids"),
        ("Peonies",       "/peonies"),
        ("Ranunculus",    "/ranunculus"),
        ("Roses",         "/roses"),
        ("Succulents",    "/succulents"),
        ("Sunflowers",    "/sunflowers"),
        ("Tropical",      "/tropical"),
        ("Tulips",        "/tulips"),
        ("Unique Stems",  "/unique-stems"),
    ]
    BASE_URL = "https://bouqs.com"
    TIMEOUT = 15000  # milliseconds

    def __init__(self):
        self.playwright = sync_playwright().start()
        self.browser = self.playwright.chromium.launch(headless=True)
        self.context = self.browser.new_context(
            user_agent=(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/114.0.0.0 Safari/537.36"
            )
        )
        self.page = self.context.new_page()

    def build_full_url(self, relative_path: str) -> str:
        if relative_path.startswith("/flowers/"):
            return f"{self.BASE_URL}{relative_path}"
        return f"{self.BASE_URL}/flowers{relative_path}"

    def fetch_page_source(self, url: str) -> str | None:
        try:
            self.page.goto(url, timeout=self.TIMEOUT)
            self.page.wait_for_selector(
                "#main > div.content-area.content-area--1.css-0 > div.category-banner-block.css-uy79w0",
                timeout=self.TIMEOUT,
            )
            return self.page.content()
        except PlaywrightTimeoutError:
            print(f"Warning: Timeout while loading {url}")
            return None
        except Exception as e:
            print(f"Warning: Failed to load {url} → {e}")
            return None

    def crawl_category(self, name: str, relative_path: str) -> dict[str, str]:
        full_url = self.build_full_url(relative_path)
        html = self.fetch_page_source(full_url)

        header = ""
        description = ""
        image_url = ""

        if html:
            from bs4 import BeautifulSoup

            soup = BeautifulSoup(html, "html.parser")

            header_tag = soup.select_one(
                "#main > div.content-area.content-area--1.css-0 "
                "> div.category-banner-block.css-uy79w0 > div > h1"
            )
            if header_tag:
                header = header_tag.get_text(strip=True)

            desc_div = soup.select_one(
                "#main > div.content-area.content-area--1.css-0 "
                "> div.category-banner-block.css-uy79w0 > div > div"
            )
            if desc_div:
                description = desc_div.get_text(separator=" ", strip=True)

            img_tag = soup.select_one(
                "#main > div.content-area.content-area--1.css-0 "
                "> div.category-banner-block.css-uy79w0 > img"
            )
            if img_tag and img_tag.get("src"):
                raw_src = img_tag["src"]
                image_url = raw_src if raw_src.startswith("http") else f"{self.BASE_URL}{raw_src}"

        return {
            "name": name,
            "relative_path": relative_path,
            "full_url": full_url,
            "header": header,
            "description": description,
            "image_url": image_url,
        }

    def crawl_all(self) -> list[dict[str, str]]:
        results = []
        for name, path in self.categories:
            entry = self.crawl_category(name, path)
            results.append(entry)
        return results

    def close(self):
        self.browser.close()
        self.playwright.stop()


class ProductCrawler:
    BASE_URL = "https://bouqs.com"
    TIMEOUT = 15000  # milliseconds

    def __init__(self, category_name: str, category_relative_path: str):
        self.category_name = category_name
        self.category_path = category_relative_path

        self.playwright = sync_playwright().start()
        self.browser = self.playwright.chromium.launch(headless=True)
        self.context = self.browser.new_context(
            user_agent=(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/114.0.0.0 Safari/537.36"
            )
        )
        self.page = self.context.new_page()

    def build_category_url(self) -> str:
        if self.category_path.startswith("/flowers/"):
            return f"{self.BASE_URL}{self.category_path}"
        return f"{self.BASE_URL}/flowers{self.category_path}"

    def fetch_page_source(self, url: str, selector: str) -> str | None:
        try:
            self.page.goto(url, timeout=self.TIMEOUT)
            self.page.wait_for_selector(selector, timeout=self.TIMEOUT)
            return self.page.content()
        except PlaywrightTimeoutError:
            print(f"Warning: Timeout while loading {url}")
            return None
        except Exception as e:
            print(f"Warning: Failed to load {url} → {e}")
            return None

    def crawl_category_product_urls(self) -> list[str]:
        url = self.build_category_url()
        selector = "#main > div.product-grid.product-grid--show-subscriber.css-1f4s17w > div > a"
        html = self.fetch_page_source(url, selector)
        product_links = set()

        if html:
            from bs4 import BeautifulSoup

            soup = BeautifulSoup(html, "html.parser")
            grid = soup.select_one("#main > div.product-grid.product-grid--show-subscriber.css-1f4s17w")
            if grid:
                for link_tag in grid.select("div > a"):
                    href = link_tag.get("href")
                    if not href:
                        continue
                    full_link = href if href.startswith("http") else f"{self.BASE_URL}{href}"
                    product_links.add(full_link)

        return list(product_links)

    def crawl_product(self, product_url: str) -> dict[str, any]:
        try:
            self.page.goto(product_url, timeout=self.TIMEOUT)
            self.page.wait_for_selector("#pdp_regular_price > span > span.price__number", timeout=self.TIMEOUT)
        except PlaywrightTimeoutError:
            print(f"Warning: Timeout while loading product {product_url}")
            return {}
        except Exception as e:
            print(f"Warning: Failed to load product {product_url} → {e}")
            return {}

        # Click "Read More" if present
        try:
            button_selector = (
                "#main > div > div > div > div "
                "> section.product-details__section.product-details__section--featured"
                ".product-details__section--regular.text-center "
                "> div > div.product-information-section "
                "> div.product-details__description > button"
            )
            if self.page.is_visible(button_selector):
                self.page.click(button_selector)
                self.page.wait_for_selector(button_selector, state="hidden", timeout=self.TIMEOUT)
        except Exception:
            pass

        html = self.page.content()
        from bs4 import BeautifulSoup

        soup = BeautifulSoup(html, "html.parser")

        name_tag = soup.find("h1")
        name = name_tag.get_text(strip=True) if name_tag else ""

        price_tag = soup.select_one("#pdp_regular_price > span > span.price__number")
        price = price_tag.get_text(strip=True) if price_tag else ""

        image_urls = []
        seen = set()
        meta_tags = soup.select(
            "#main > div > div > div > div "
            "> section.product-details__section.product-details__section--featured"
            ".product-details__section--regular.text-center "
            "> div > div.product-information-section "
            "> div.product-details__media "
            "> div.product-details__alternate-image-group.slick-initialized.slick-slider "
            "> div > div > div > div > div > meta"
        )
        for meta in meta_tags:
            content = meta.get("content", "").strip()
            if content.startswith("http") and "small_thumb" not in content and content not in seen:
                seen.add(content)
                image_urls.append(content)

        desc_html = ""
        desc_text = ""
        desc_div = soup.select_one(
            "#main > div > div > div > div "
            "> section.product-details__section.product-details__section--featured"
            ".product-details__section--regular.text-center "
            "> div > div.product-information-section "
            "> div.product-details__description"
        )
        if desc_div:
            desc_html = desc_div.decode_contents().replace(
                '<button class="product-details__description-show-overflow-button '
                'font-uppercase product-details__description-show-overflow-button--show">'
                'Read Less</button>',
                ""
            ).strip()
            desc_text = desc_div.get_text(separator=" ", strip=True)
            if desc_text.endswith("Read Less"):
                desc_text = desc_text[: -len("Read Less")].strip()

        return {
            "category_name": self.category_name,
            "product_url": product_url,
            "name": name,
            "price": price,
            "image_urls": image_urls,
            "description_html": desc_html,
            "description_text": desc_text,
        }

    def crawl_all_products(self) -> list[dict[str, any]]:
        details = []
        product_urls = self.crawl_category_product_urls()
        for url in product_urls:
            detail = self.crawl_product(url)
            if detail:
                details.append(detail)
        return details

    def close(self):
        self.browser.close()
        self.playwright.stop()


if __name__ == "__main__":
    # Crawl categories → save to JSON
    cat_crawler = CategoryCrawler()
    categories_data = cat_crawler.crawl_all()
    cat_crawler.close()

    os.makedirs("data/categories", exist_ok=True)
    with open("data/categories/categories.json", "w", encoding="utf-8") as f:
        json.dump(categories_data, f, ensure_ascii=False, indent=4)

    # Crawl each category’s products → save to JSON
    category_names, category_paths = zip(*CategoryCrawler.categories)
    os.makedirs("data/products", exist_ok=True)

    for name, path in zip(category_names, category_paths):
        prod_crawler = ProductCrawler(name, path)
        products_data = prod_crawler.crawl_all_products()
        prod_crawler.close()

        with open(f"data/products/{name}.json", "w", encoding="utf-8") as f:
            json.dump(products_data, f, ensure_ascii=False, indent=4)
