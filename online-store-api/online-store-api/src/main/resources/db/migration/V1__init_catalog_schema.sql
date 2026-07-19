CREATE TABLE category (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE product (
    id             BIGSERIAL PRIMARY KEY,
    sku            VARCHAR(50)  NOT NULL UNIQUE,
    name           VARCHAR(150) NOT NULL,
    description    VARCHAR(1000),
    price          NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
    category_id    BIGINT NOT NULL REFERENCES category (id),
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_category_id ON product (category_id);
CREATE INDEX idx_product_active ON product (active);
CREATE INDEX idx_product_name ON product (lower(name));
