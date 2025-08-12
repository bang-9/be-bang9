CREATE TABLE bang9.agency
(
    id          UUID         NOT NULL,
    status      BOOLEAN      NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    contact     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_agency PRIMARY KEY (id)
);

CREATE TABLE bang9."user"
(
    id                     UUID                         NOT NULL,
    status                 BOOLEAN                      NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
    modified_at            TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
    email                  VARCHAR(255)                 NOT NULL,
    password               VARCHAR(255),
    nickname               VARCHAR(255)                 NOT NULL,
    role                   VARCHAR(255) DEFAULT 'USER',
    provider               VARCHAR(255) DEFAULT 'EMAIL' NOT NULL,
    representing_agency_id UUID,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE bang9.user_agency_membership
(
    agency_id UUID NOT NULL,
    user_id   UUID NOT NULL,
    CONSTRAINT pk_user_agency_membership PRIMARY KEY (agency_id, user_id)
);

ALTER TABLE bang9."user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE bang9."user"
    ADD CONSTRAINT uc_user_nickname UNIQUE (nickname);

ALTER TABLE bang9."user"
    ADD CONSTRAINT uc_user_representing_agency UNIQUE (representing_agency_id);

CREATE INDEX idx_agency_name ON bang9.agency (name);

CREATE INDEX idx_user_email ON bang9."user" (email);

CREATE INDEX idx_user_nickname ON bang9."user" (nickname);