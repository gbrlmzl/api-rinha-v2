-- V1__create_users
CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    active      BOOLEAN      NOT NULL    DEFAULT TRUE,
    role        VARCHAR(30) NOT NULL DEFAULT 'USER',
    nickname    VARCHAR(100) NOT NULL,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100),
    profile_pic VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE user_auth_providers
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    provider    VARCHAR(50)  NOT NULL,
    provider_id VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_provider UNIQUE (provider, provider_id)
);