
CREATE TABLE pricing_rule_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pricing_rule_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (pricing_rule_id) REFERENCES pricing_rules(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_rule_product (pricing_rule_id, product_id)
);

CREATE TABLE pricing_rule_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pricing_rule_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (pricing_rule_id) REFERENCES pricing_rules(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_rule_category (pricing_rule_id, category_id)
);

INSERT INTO pricing_rule_products (pricing_rule_id, product_id)
SELECT id, product_id 
FROM pricing_rules 
WHERE product_id IS NOT NULL;

INSERT INTO pricing_rule_categories (pricing_rule_id, category_id)
SELECT id, category_id 
FROM pricing_rules 
WHERE category_id IS NOT NULL;

ALTER TABLE pricing_rules DROP COLUMN product_id;
ALTER TABLE pricing_rules DROP COLUMN category_id;

CREATE INDEX idx_pricing_rule_products_rule_id ON pricing_rule_products(pricing_rule_id);
CREATE INDEX idx_pricing_rule_products_product_id ON pricing_rule_products(product_id);
CREATE INDEX idx_pricing_rule_categories_rule_id ON pricing_rule_categories(pricing_rule_id);
CREATE INDEX idx_pricing_rule_categories_category_id ON pricing_rule_categories(category_id);
