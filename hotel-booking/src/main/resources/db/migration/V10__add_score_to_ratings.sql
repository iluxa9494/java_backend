ALTER TABLE ratings ADD COLUMN IF NOT EXISTS score INT;

UPDATE ratings
SET score = rating
WHERE score IS NULL;

ALTER TABLE ratings
  ALTER COLUMN score SET NOT NULL;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'chk_ratings_score'
      AND conrelid = 'ratings'::regclass
  ) THEN
    ALTER TABLE ratings
      ADD CONSTRAINT chk_ratings_score CHECK (score BETWEEN 1 AND 5);
  END IF;
END$$;
