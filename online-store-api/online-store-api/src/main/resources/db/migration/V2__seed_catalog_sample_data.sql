INSERT INTO category (name, description) VALUES
    ('Electronics', 'Phones, laptops, and accessories'),
    ('Home & Kitchen', 'Appliances and kitchenware'),
    ('Books', 'Fiction, non-fiction, and technical books'),
    ('Sportswear', 'Athletic clothing and footwear');

INSERT INTO product (sku, name, description, price, stock_quantity, category_id, active) VALUES
    ('ELEC-0001', 'Wireless Mouse',        'Ergonomic 2.4GHz wireless mouse',       12.99, 150, 1, TRUE),
    ('ELEC-0002', 'Mechanical Keyboard',   'RGB backlit mechanical keyboard',       59.99, 80,  1, TRUE),
    ('ELEC-0003', '27" Monitor',           'QHD 27-inch IPS monitor',              229.00, 25,  1, TRUE),
    ('ELEC-0004', 'USB-C Charger 65W',     'Fast-charging GaN power adapter',       24.50, 200, 1, TRUE),
    ('HOME-0001', 'Stainless Steel Kettle','1.7L electric kettle',                  34.99, 60,  2, TRUE),
    ('HOME-0002', 'Non-Stick Frying Pan',  '28cm non-stick frying pan',              19.99, 90,  2, TRUE),
    ('HOME-0003', 'Coffee Maker',          '12-cup drip coffee maker',              45.00, 40,  2, TRUE),
    ('BOOK-0001', 'Clean Code',            'A handbook of agile software craftsmanship', 32.00, 55, 3, TRUE),
    ('BOOK-0002', 'Effective Java',        '3rd edition, best practices for Java',   38.50, 45, 3, TRUE),
    ('SPRT-0001', 'Running Shoes',         'Lightweight breathable running shoes',   64.99, 70, 4, TRUE),
    ('SPRT-0002', 'Yoga Mat',              'Non-slip 6mm yoga mat',                  18.00, 120,4, TRUE),
    ('SPRT-0003', 'Adjustable Dumbbells',  '2x adjustable dumbbells, 2-20kg each',  120.00, 15, 4, TRUE);
