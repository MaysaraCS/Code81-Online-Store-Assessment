CREATE TABLE payment (
    id                          BIGSERIAL PRIMARY KEY,
    order_id                    BIGINT NOT NULL UNIQUE REFERENCES orders (id),
    stripe_checkout_session_id  VARCHAR(200) NOT NULL UNIQUE,
    stripe_payment_intent_id    VARCHAR(200),
    amount                      NUMERIC(12, 2) NOT NULL CHECK (amount >= 0),
    currency                    VARCHAR(3) NOT NULL DEFAULT 'usd',
    status                      VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCEEDED', 'FAILED')),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_payment_order_id ON payment (order_id);
