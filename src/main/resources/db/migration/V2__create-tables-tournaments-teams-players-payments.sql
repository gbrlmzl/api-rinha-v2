--create_tournaments
CREATE TABLE tournaments
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100)   NOT NULL,
    game       VARCHAR(50)    NOT NULL,
    status     VARCHAR(30)    NOT NULL DEFAULT 'OPEN',
    max_teams  INTEGER        NOT NULL DEFAULT 16,
    prize_pool DECIMAL(10, 2),
    starts_at  TIMESTAMP WITH TIME ZONE,
    ends_at    TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

--create_teams
CREATE TABLE teams
(
    id            BIGSERIAL PRIMARY KEY,
    captain_id    BIGINT       NOT NULL,
    tournament_id BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    shield_url    TEXT,
    status        VARCHAR(30)  NOT NULL DEFAULT 'PENDING_PAYMENT',
    active        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_captain              FOREIGN KEY (captain_id)    REFERENCES users(id)       ON DELETE RESTRICT,
    CONSTRAINT fk_tournament           FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE RESTRICT,
    CONSTRAINT uq_captain_tournament   UNIQUE (captain_id, tournament_id),
    CONSTRAINT uq_team_name_tournament UNIQUE (name, tournament_id)
);

--create_players
CREATE TABLE players
(
    id            BIGSERIAL PRIMARY KEY,
    team_id       BIGINT       NOT NULL,
    user_id       BIGINT,
    name          VARCHAR(100) NOT NULL,
    school_id     VARCHAR(50),
    external_player BOOLEAN      NOT NULL DEFAULT FALSE,
    discord       VARCHAR(100) NOT NULL,
    nickname      VARCHAR(100) NOT NULL,
    role          VARCHAR(30),
    active        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_player_team  FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_player_user  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_nickname_team UNIQUE (nickname, team_id)
);

--create_payments
CREATE TABLE payments
(
    id              BIGSERIAL PRIMARY KEY,
    team_id         BIGINT         NOT NULL UNIQUE,
    mercado_pago_id VARCHAR(100)   UNIQUE,
    uuid            VARCHAR(50)    NOT NULL UNIQUE,
    status          VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    status_detail   VARCHAR(100),
    qr_code         TEXT,
    value           DECIMAL(10, 2) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    paid_at         TIMESTAMP WITH TIME ZONE,
    payer           VARCHAR(100),

    CONSTRAINT fk_payment_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);