CREATE TABLE tariff_settings (
    id SERIAL PRIMARY KEY,
    volume_rate NUMERIC(10,2) NOT NULL,
    weight_rate NUMERIC(10,4) NOT NULL,
    minimal_price NUMERIC(10,2) NOT NULL,
    distance_step_km INT NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);