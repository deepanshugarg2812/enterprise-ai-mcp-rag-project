CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    order_number VARCHAR(50) NOT NULL UNIQUE,

    customer_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,

    total_amount_paise BIGINT NOT NULL CHECK (total_amount_paise >= 0),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);