--liquibase formatted sql

--changeset nikitin:1
INSERT INTO product (id, name, vendor_code, description, category, price, amount, last_amount_up, create_at)
VALUES ('54a36c6d-eeea-4249-891f-70001ac6ad1e', 'Iphone', 123, 'Мобильный телефон компании Apple', 'MOBILE', 42000.05,
        10, '2015-07-30T16:29:11', '2015-07-30T16:29:11'),
       ('3e9bd5ef-0a49-490f-bb74-c6282f8a56d9', 'Asus Laptop', 22, 'Ноутбук компании Asus', 'LAPTOP', 70000.82,
        33, '2015-07-30T16:29:11', '2015-07-30T16:29:11'),
       ('8a9e75ff-5beb-4ed2-ae8a-fb6711c8bc38', 'Xiaomi-tv', 3312, 'Телевизор компании Xiaomi', 'TV', 32000.105,
        100, '2015-07-30T16:29:11', '2015-07-30T16:29:11');

