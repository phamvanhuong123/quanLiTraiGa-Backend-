-- Add created_at and updated_at columns to materials table
ALTER TABLE materials ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE materials ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Add created_at and updated_at columns to coops table
ALTER TABLE coops ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE coops ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Add created_at and updated_at columns to suppliers table
ALTER TABLE suppliers ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE suppliers ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Backfill existing rows with a default value for created_at to satisfy the 'not null' constraint that will be applied by Hibernate
-- We use a fixed timestamp to indicate that these were backfilled.
UPDATE materials SET created_at = NOW() WHERE created_at IS NULL;
UPDATE coops SET created_at = NOW() WHERE created_at IS NULL;
UPDATE suppliers SET created_at = NOW() WHERE created_at IS NULL;
