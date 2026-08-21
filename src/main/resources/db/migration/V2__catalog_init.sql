CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.category (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    parent_id UUID,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_category_slug UNIQUE (slug),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES catalog.category (id)
);

CREATE INDEX idx_category_parent_id ON catalog.category (parent_id);

CREATE TABLE catalog.product (
    id UUID NOT NULL,
    sku TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    price_amount NUMERIC(19, 2),
    price_currency VARCHAR(3),
    category_id UUID NOT NULL,
    attributes JSONB,
    status TEXT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_product_sku UNIQUE (sku),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES catalog.category (id)
);

CREATE INDEX idx_product_category_id ON catalog.product (category_id);
CREATE INDEX idx_product_status ON catalog.product (status);
CREATE INDEX idx_product_status_price ON catalog.product (status, price_amount);
