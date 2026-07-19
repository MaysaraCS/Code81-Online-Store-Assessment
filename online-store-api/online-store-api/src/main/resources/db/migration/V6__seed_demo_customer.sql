-- Demo customer login: email=jane.doe@example.com  password=Customer@123
-- Hash below is BCrypt of "Customer@123" (generated offline; verified against
-- Spring Security's BCryptPasswordEncoder, which accepts $2a$/$2b$/$2y$ hashes).
INSERT INTO customer (first_name, last_name, email, password_hash, phone) VALUES
    ('Jane', 'Doe', 'jane.doe@example.com', '$2b$10$c/UrRl15znJTFbsZzE5DDe5Ki7rMVgpTO.6LA4rtP4vID43eW3d5S', '+201234567890');

INSERT INTO address (customer_id, label, line1, city, country, is_default)
SELECT id, 'Home', '12 Nile Street', 'Cairo', 'Egypt', TRUE FROM customer WHERE email = 'jane.doe@example.com';
