CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL CHECK (quantity > 0),

    price_at_purchase_paise BIGINT NOT NULL CHECK (price_at_purchase_paise >= 0),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);