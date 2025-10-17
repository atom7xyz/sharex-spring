ALTER TABLE file_upload
    ADD hash VARCHAR(255);

ALTER TABLE file_upload
    ALTER COLUMN hash SET NOT NULL;

ALTER TABLE file_upload
    ADD CONSTRAINT uc_fileupload_hash UNIQUE (hash);

ALTER TABLE file_upload
    DROP COLUMN md5;