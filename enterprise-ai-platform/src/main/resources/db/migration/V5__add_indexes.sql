CREATE INDEX idx_orders_customer_id
    ON orders(customer_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_orders_created_at
    ON orders(created_at);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);

CREATE INDEX idx_order_items_product_id
    ON order_items(product_id);