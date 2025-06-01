import os
import json
from dotenv import load_dotenv
import mysql.connector
from mysql.connector import errorcode

class DBWriter:
    def __init__(self):
        # Load database configuration from .env
        load_dotenv()
        self.DB_HOST     = os.getenv("DB_HOST")
        self.DB_PORT     = int(os.getenv("DB_PORT", "3306"))
        self.DB_USER     = os.getenv("DB_USER")
        self.DB_PASSWORD = os.getenv("DB_PASSWORD")
        self.DB_NAME     = os.getenv("DB_NAME")

        # Establish a connection to MySQL
        try:
            self.conn = mysql.connector.connect(
                host=self.DB_HOST,
                port=self.DB_PORT,
                user=self.DB_USER,
                password=self.DB_PASSWORD,
                database=self.DB_NAME
            )
            self.conn.autocommit = False
            self.cursor = self.conn.cursor()
        except mysql.connector.Error as err:
            if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
                print("Error: Invalid username or password")
            elif err.errno == errorcode.ER_BAD_DB_ERROR:
                print(f"Error: Database '{self.DB_NAME}' does not exist")
            else:
                print(f"Error: {err}")
            raise

    def create_tables(self):
        create_categories = """
        CREATE TABLE IF NOT EXISTS categories (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL UNIQUE,
            relative_path VARCHAR(255),
            header TEXT,
            description TEXT,
            image_url VARCHAR(512)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """

        create_products = """
        CREATE TABLE IF NOT EXISTS products (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            category_id BIGINT NOT NULL,
            product_path VARCHAR(255) NOT NULL UNIQUE,
            name VARCHAR(255),
            price VARCHAR(50),
            description_html TEXT,
            description_text TEXT,
            FOREIGN KEY (category_id)
                REFERENCES categories(id)
                ON DELETE CASCADE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """

        create_product_images = """
        CREATE TABLE IF NOT EXISTS product_images (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            product_id BIGINT NOT NULL,
            image_url VARCHAR(512) NOT NULL,
            image_order INT NOT NULL,
            UNIQUE KEY uq_product_image (product_id, image_url),
            FOREIGN KEY (product_id)
                REFERENCES products(id)
                ON DELETE CASCADE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """

        try:
            self.cursor.execute(create_categories)
            self.cursor.execute(create_products)
            self.cursor.execute(create_product_images)
            self.conn.commit()
            print("Tables 'categories', 'products', and 'product_images' ensured.")
        except mysql.connector.Error as err:
            print(f"Failed creating tables: {err}")
            self.conn.rollback()
            raise

    def write_from_json(self):
        # 1) Load categories JSON
        categories_file = os.path.join('data', 'categories', 'categories.json')
        try:
            with open(categories_file, 'r', encoding='utf-8') as f:
                categories_data = json.load(f)
        except Exception as e:
            print(f"Error loading categories JSON: {e}")
            return

        # 2) Insert or update each category (strip leading '/' from relative_path)
        insert_category = """
            INSERT INTO categories
                (name, relative_path, header, description, image_url)
            VALUES (%s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                relative_path = VALUES(relative_path),
                header = VALUES(header),
                description = VALUES(description),
                image_url = VALUES(image_url)
        """
        for cat in categories_data:
            rel = cat.get('relative_path', '').lstrip('/')
            try:
                self.cursor.execute(
                    insert_category,
                    (
                        cat.get('name'),
                        rel,
                        cat.get('header'),
                        cat.get('description'),
                        cat.get('image_url')
                    )
                )
                self.conn.commit()
            except mysql.connector.Error as err:
                print(f"Error inserting category '{cat.get('name')}': {err}")
                self.conn.rollback()
                continue

        # 3) Fetch each category's ID from the database
        select_category_id = "SELECT id FROM categories WHERE name = %s"
        category_ids = {}
        try:
            for cat in categories_data:
                name = cat.get('name')
                self.cursor.execute(select_category_id, (name,))
                row = self.cursor.fetchone()
                if row:
                    category_ids[name] = row[0]
        except mysql.connector.Error as err:
            print(f"Error fetching category IDs: {err}")
            raise

        # 4) For each category, load its products JSON and insert/update them
        insert_product = """
            INSERT INTO products
                (category_id, product_path, name, price, description_html, description_text)
            VALUES (%s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                price = VALUES(price),
                description_html = VALUES(description_html),
                description_text = VALUES(description_text)
        """
        select_product_id = "SELECT id FROM products WHERE product_path = %s"
        insert_image = """
            INSERT INTO product_images
                (product_id, image_url, image_order)
            VALUES (%s, %s, %s)
            ON DUPLICATE KEY UPDATE image_order = VALUES(image_order)
        """

        for cat in categories_data:
            name = cat.get('name')
            cat_id = category_ids.get(name)
            if not cat_id:
                print(f"Skipping products for '{name}': no category ID found.")
                continue

            products_file = os.path.join('data', 'products', f"{name}.json")
            if not os.path.isfile(products_file):
                print(f"Products JSON not found for '{name}' at {products_file}")
                continue

            try:
                with open(products_file, 'r', encoding='utf-8') as f:
                    products_data = json.load(f)
            except Exception as e:
                print(f"Error loading products JSON for '{name}': {e}")
                continue

            for prod in products_data:
                # Compute product_path by taking last segment of product_url
                full_url = prod.get('product_url', '')
                product_path = full_url.strip('/').split('/')[-1]

                try:
                    # Insert or update product record
                    self.cursor.execute(
                        insert_product,
                        (
                            cat_id,
                            product_path,
                            prod.get('name'),
                            prod.get('price'),
                            prod.get('description_html'),
                            prod.get('description_text')
                        )
                    )
                    self.conn.commit()
                except mysql.connector.Error as err:
                    print(f"Error inserting product '{product_path}': {err}")
                    self.conn.rollback()
                    continue

                # Fetch the product_id we just inserted/updated
                try:
                    self.cursor.execute(select_product_id, (product_path,))
                    row = self.cursor.fetchone()
                    if not row:
                        print(f"Could not retrieve product ID for '{product_path}'")
                        continue
                    product_id = row[0]
                except mysql.connector.Error as err:
                    print(f"Error fetching product ID for '{product_path}': {err}")
                    continue

                # Insert each image URL into product_images
                image_urls = prod.get('image_urls', [])
                for idx, img_url in enumerate(image_urls):
                    try:
                        self.cursor.execute(
                            insert_image,
                            (product_id, img_url, idx)
                        )
                        self.conn.commit()
                    except mysql.connector.Error as err:
                        print(f"Error inserting image for product '{product_path}': {err}")
                        self.conn.rollback()
                        continue

    def close(self):
        # Close database cursor and connection
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()


if __name__ == '__main__':
    writer = DBWriter()
    writer.create_tables()
    writer.write_from_json()
    writer.close()
