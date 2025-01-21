CREATE TABLE SHORTENED_URL
(
    ID         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ORIGIN_URL VARCHAR(255),
    TARGET_URL VARCHAR(255),
    CONSTRAINT CONSTRAINT_E PRIMARY KEY (ID)
);