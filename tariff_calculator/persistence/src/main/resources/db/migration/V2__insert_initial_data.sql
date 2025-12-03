-- Стартовая валюта
INSERT INTO currency (code, name, rate_to_rub)
VALUES ('RUB', 'Российский рубль', 1.000000);

-- Стартовые тарифы
INSERT INTO tariff_settings (
  volume_rate, weight_rate, minimal_price, distance_step_km, currency_code
)
VALUES (
  1200.00, 0.04, 500.00, 450, 'RUB'
);