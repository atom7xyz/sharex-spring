ALTER TABLE shortened_url
    ADD CONSTRAINT uc_shortenedurl_originurl UNIQUE (origin_url);

ALTER TABLE shortened_url
    ADD CONSTRAINT uc_shortenedurl_targeturl UNIQUE (target_url);

ALTER TABLE shortened_url
    ALTER COLUMN origin_url SET NOT NULL;

ALTER TABLE shortened_url
    ALTER COLUMN target_url SET NOT NULL;