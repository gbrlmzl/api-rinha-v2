CREATE TABLE users
(
    id         BIGINT  PRIMARY KEY AUTO_INCREMENT,
    active tinyint NOT NULL DEFAULT 1,
    nickname       VARCHAR(100) NOT NULL,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100),
    profile_Pic VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_auth_providers
(
    id         BINARY(16)  PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    userId     BIGINT   NOT NULL,
    provider   VARCHAR(50)  NOT NULL,
    providerId VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_provider UNIQUE (provider, providerId)
);



