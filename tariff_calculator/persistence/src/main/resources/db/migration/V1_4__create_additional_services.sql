CREATE TABLE additional_services (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    price_type VARCHAR(10) NOT NULL CHECK (price_type IN ('fixed', 'percent')),
    description TEXT
);