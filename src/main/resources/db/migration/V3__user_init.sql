CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users.account (
    id UUID NOT NULL,
    email TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    role TEXT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_account_email UNIQUE (email)
);

CREATE TABLE users.address (
    id UUID NOT NULL,
    account_id UUID NOT NULL,
    label TEXT NOT NULL,
    street TEXT NOT NULL,
    city TEXT NOT NULL,
    postal_code TEXT NOT NULL,
    country TEXT NOT NULL,
    is_default BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_address_account FOREIGN KEY (account_id) REFERENCES users.account (id)
);

CREATE INDEX idx_address_account_id ON users.address (account_id);

CREATE TABLE users.refresh_token (
    id UUID NOT NULL,
    account_id UUID NOT NULL,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_token_account FOREIGN KEY (account_id) REFERENCES users.account (id)
);

CREATE INDEX idx_refresh_token_account_id ON users.refresh_token (account_id);
