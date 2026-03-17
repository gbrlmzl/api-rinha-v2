CREATE TABLE users
(
    id         BINARY(16)  PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    nickname       VARCHAR(100) NOT NULL,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100),
    profilePic VARCHAR(255),
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_auth_providers
(
    id         BINARY(16)  PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    userId     BINARY(16)   NOT NULL,
    provider   VARCHAR(50)  NOT NULL,
    providerId VARCHAR(100) NOT NULL,
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_provider UNIQUE (provider, providerId)
);



