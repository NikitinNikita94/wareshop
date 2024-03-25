--liquibase formatted sql

--changeset nikitin:1
CREATE TABLE IF NOT EXISTS product
(
    id             uuid PRIMARY KEY,
    name           VARCHAR(64)  NOT NULL,
    vendor_code    INT          NOT NULL UNIQUE,
    description    varchar(256) NOT NULL,
    category       varchar(64)  NOT NULL,
    price          DECIMAL      NOT NULL,
    amount         INT          NOT NULL,
    last_amount_up TIMESTAMP    NOT NULL,
    create_at      TIMESTAMP    NOT NULL
);
--rollback DROP TABLE product
