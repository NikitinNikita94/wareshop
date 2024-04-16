create function gen_random_uuid() RETURNS uuid as
$$
SELECT md5(random()::text || clock_timestamp()::text)::uuid
$$ LANGUAGE SQL;

insert into product(id, name, vendor_code, description, category, price, amount, last_amount_up, create_at)
select gen_random_uuid(),
       'Iphone' + k,
       k,
       'Мобильный телефон компании Apple' + k,
       'MOBILE',
       '420',
       '10',
       TIMESTAMPTZ '2023-11-01',
       TIMESTAMPTZ '2023-11-07'
from generate_series(1, 1000000) as k;