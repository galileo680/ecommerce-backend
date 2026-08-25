INSERT INTO users.account (id, email, password_hash, first_name, last_name, role, created_at, updated_at) VALUES
    ('33333333-3333-3333-3333-333333333301', 'admin@ecommerce.local', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Local', 'Admin', 'ADMIN', now(), now())
ON CONFLICT (id) DO NOTHING;
