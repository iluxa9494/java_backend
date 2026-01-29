DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'rooms'
      AND column_name = 'price'
      AND NOT (data_type = 'numeric' AND numeric_precision = 10 AND numeric_scale = 2)
  ) THEN
    ALTER TABLE public.rooms
      ALTER COLUMN price TYPE numeric(10,2)
      USING round(price::numeric, 2);
  END IF;
END $$;
