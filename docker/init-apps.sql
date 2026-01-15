CREATE DATABASE currency;
CREATE DATABASE hotel_booking;
CREATE DATABASE cryptobot;
CREATE DATABASE tariff_calculator;

CREATE USER hotel_admin WITH PASSWORD 'postgres';
ALTER DATABASE hotel_booking OWNER TO hotel_admin;

CREATE USER cryptobot WITH PASSWORD 'cryptobot_password_123';
ALTER DATABASE cryptobot OWNER TO cryptobot;

CREATE USER tariff_calculator WITH PASSWORD 'tariff_calculator_password';
ALTER DATABASE tariff_calculator OWNER TO tariff_calculator;
