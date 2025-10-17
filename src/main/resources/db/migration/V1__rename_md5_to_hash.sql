DO $$
    BEGIN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name='file_upload' AND column_name='md5'
        ) THEN
            ALTER TABLE file_upload RENAME COLUMN md5 TO hash;
        END IF;
    END
$$;