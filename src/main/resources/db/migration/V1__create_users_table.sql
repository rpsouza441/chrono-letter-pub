CREATE TABLE users (
    id                  UUID PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    email_verified      BOOLEAN NOT NULL DEFAULT FALSE,
    timezone            VARCHAR(50) NOT NULL,
    auth_method         VARCHAR(20),
    oauth_provider      VARCHAR(50),
    oauth_subject       VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_oauth ON users (oauth_provider, oauth_subject) WHERE oauth_provider IS NOT NULL;