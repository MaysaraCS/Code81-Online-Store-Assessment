CREATE TABLE staff_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL CHECK (role IN ('ADMIN', 'STORE_MANAGER', 'SUPPORT_AGENT')),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE refresh_token (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(100) NOT NULL UNIQUE,
    owner_type  VARCHAR(20) NOT NULL CHECK (owner_type IN ('CUSTOMER', 'STAFF')),
    owner_id    BIGINT NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_owner ON refresh_token (owner_type, owner_id);

-- Seeded admin account so the API is demoable immediately without a
-- chicken-and-egg "who creates the first admin" problem.
-- Login: email=admin@code81.local  password=Admin@123
-- Hash below is BCrypt of "Admin@123" - change this password after first login in any real deployment.
INSERT INTO staff_user (username, email, password_hash, role, active) VALUES
    ('admin', 'admin@code81.local', '$2b$10$l3YlBqaoOqIcFm1TkQCOqOla5NbAexsU1q45vaPltUX.FkUNHK4Ei', 'ADMIN', TRUE);
