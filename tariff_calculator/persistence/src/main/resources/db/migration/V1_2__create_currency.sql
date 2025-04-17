CREATE TABLE currency (
    code VARCHAR(3) PRIMARY KEY,
    name VARCHAR,
    rate_to_rub NUMERIC(12,6) NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW()
);