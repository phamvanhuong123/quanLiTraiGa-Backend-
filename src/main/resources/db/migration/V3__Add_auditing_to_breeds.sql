-- Add created_at and updated_at columns to the breeds table
ALTER TABLE breeds ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE breeds ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Backfill existing rows with a default value for created_at to satisfy the 'not null' constraint
UPDATE breeds SET created_at = NOW() WHERE created_at IS NULL;
