INSERT INTO catalog.category (id, name, slug, parent_id, created_at, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Electronics', 'electronics', NULL, now(), now()),
    ('11111111-1111-1111-1111-111111111112', 'Laptops', 'laptops', '11111111-1111-1111-1111-111111111111', now(), now()),
    ('11111111-1111-1111-1111-111111111113', 'Phones', 'phones', '11111111-1111-1111-1111-111111111111', now(), now()),
    ('11111111-1111-1111-1111-111111111114', 'Books', 'books', NULL, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO catalog.product (id, sku, name, description, price_amount, price_currency, category_id, attributes, status, created_at, updated_at) VALUES
    ('22222222-2222-2222-2222-222222222201', 'LAP-PRO-14', 'Pro Laptop 14', 'Light laptop for work', 5499.00, 'PLN', '11111111-1111-1111-1111-111111111112', '{"ram": "16GB", "screen": "14"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222202', 'LAP-AIR-13', 'Air Laptop 13', 'Small and quiet laptop', 4299.00, 'PLN', '11111111-1111-1111-1111-111111111112', '{"ram": "8GB", "screen": "13"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222203', 'LAP-GAM-16', 'Gaming Laptop 16', 'Fast laptop for games', 7999.00, 'PLN', '11111111-1111-1111-1111-111111111112', '{"ram": "32GB", "screen": "16"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222204', 'PHO-MAX-01', 'Max Phone', 'Large phone with a good camera', 3999.00, 'PLN', '11111111-1111-1111-1111-111111111113', '{"storage": "256GB"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222205', 'PHO-MIN-01', 'Mini Phone', 'Small phone for one hand', 2999.00, 'PLN', '11111111-1111-1111-1111-111111111113', '{"storage": "128GB"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222206', 'BOO-DDD-01', 'Domain Driven Design', 'The blue book by Eric Evans', 199.00, 'PLN', '11111111-1111-1111-1111-111111111114', '{"pages": "560"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222207', 'BOO-EIP-01', 'Enterprise Integration Patterns', 'Messaging patterns catalog', 249.00, 'PLN', '11111111-1111-1111-1111-111111111114', '{"pages": "736"}', 'ACTIVE', now(), now()),
    ('22222222-2222-2222-2222-222222222208', 'PHO-FLD-01', 'Fold Phone', 'Phone that folds in half', NULL, NULL, '11111111-1111-1111-1111-111111111113', '{}', 'DRAFT', now(), now())
ON CONFLICT (id) DO NOTHING;
