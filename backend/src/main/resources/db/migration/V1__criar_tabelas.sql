CREATE TABLE instituicao (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(150) NOT NULL,
    tipo  VARCHAR(20)  NOT NULL
);

CREATE TABLE usuario (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    senha_hash     VARCHAR(100) NOT NULL,
    perfil         VARCHAR(20)  NOT NULL,
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    instituicao_id BIGINT REFERENCES instituicao (id)
);

CREATE TABLE animal (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    especie    VARCHAR(20)  NOT NULL,
    porte      VARCHAR(50),
    descricao  TEXT,
    status     VARCHAR(20)  NOT NULL,
    criado_em  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE resgate (
    id            BIGSERIAL PRIMARY KEY,
    animal_id     BIGINT      NOT NULL REFERENCES animal (id) ON DELETE CASCADE,
    voluntario_id BIGINT      NOT NULL REFERENCES usuario (id),
    local         VARCHAR(255) NOT NULL,
    observacoes   TEXT,
    data_resgate  TIMESTAMPTZ NOT NULL
);

CREATE TABLE atendimento (
    id             BIGSERIAL PRIMARY KEY,
    animal_id      BIGINT      NOT NULL REFERENCES animal (id) ON DELETE CASCADE,
    responsavel_id BIGINT      NOT NULL REFERENCES usuario (id),
    tipo           VARCHAR(20) NOT NULL,
    descricao      TEXT        NOT NULL,
    validado       BOOLEAN     NOT NULL DEFAULT FALSE,
    data_registro  TIMESTAMPTZ NOT NULL
);

CREATE TABLE adocao (
    id                  BIGSERIAL PRIMARY KEY,
    animal_id           BIGINT       NOT NULL UNIQUE REFERENCES animal (id),
    adotante_nome       VARCHAR(150) NOT NULL,
    adotante_contato    VARCHAR(150) NOT NULL,
    registrado_por_id   BIGINT       NOT NULL REFERENCES usuario (id),
    data_adocao         TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_animal_status ON animal (status);
CREATE INDEX idx_atendimento_animal ON atendimento (animal_id, tipo);
