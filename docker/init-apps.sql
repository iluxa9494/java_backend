DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'currency') THEN
    CREATE DATABASE currency;
  END IF;
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hotel_booking') THEN
    CREATE DATABASE hotel_booking;
  END IF;
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cryptobot') THEN
    CREATE DATABASE cryptobot;
  END IF;
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tariff_calculator') THEN
    CREATE DATABASE tariff_calculator;
  END IF;

  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'hotel_admin') THEN
    CREATE USER hotel_admin WITH PASSWORD 'postgres';
  END IF;
  ALTER DATABASE hotel_booking OWNER TO hotel_admin;

  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'currency_exchange') THEN
    CREATE USER currency_exchange WITH PASSWORD 'currency_exchange_password';
  END IF;
  ALTER DATABASE currency OWNER TO currency_exchange;

  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'cryptobot') THEN
    CREATE USER cryptobot WITH PASSWORD 'cryptobot_password_123';
  END IF;
  ALTER DATABASE cryptobot OWNER TO cryptobot;

  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'tariff_calculator') THEN
    CREATE USER tariff_calculator WITH PASSWORD 'tariff_calculator_password';
  END IF;
  ALTER DATABASE tariff_calculator OWNER TO tariff_calculator;
END
$$;

GRANT CONNECT ON DATABASE tariff_calculator TO tariff_calculator;

\connect tariff_calculator

GRANT USAGE, CREATE ON SCHEMA public TO tariff_calculator;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO tariff_calculator;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO tariff_calculator;

GRANT CONNECT ON DATABASE currency TO currency_exchange;

\connect currency

GRANT USAGE, CREATE ON SCHEMA public TO currency_exchange;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO currency_exchange;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO currency_exchange;

GRANT CONNECT ON DATABASE cryptobot TO cryptobot;

\connect cryptobot

GRANT USAGE, CREATE ON SCHEMA public TO cryptobot;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO cryptobot;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO cryptobot;
