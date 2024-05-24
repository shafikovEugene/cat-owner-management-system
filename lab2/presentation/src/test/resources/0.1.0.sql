CREATE TABLE IF NOT EXISTS owners (
    owner_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    birth_date DATE
);

CREATE TABLE IF NOT EXISTS cats (
    cat_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    breed VARCHAR(100),
    color VARCHAR(100),
    owner_id BIGINT REFERENCES owners(owner_id)
);

CREATE TABLE IF NOT EXISTS cat_friendships (
    cat_id BIGINT REFERENCES cats(cat_id),
    friend_cat_id BIGINT REFERENCES cats(cat_id),
    PRIMARY KEY (cat_id, friend_cat_id)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS owners_roles (
    owner_id BIGINT NOT NULL REFERENCES owners(owner_id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (owner_id, role_id)
);

INSERT INTO roles (name)
VALUES
('ROLE_USER'), ('ROLE_ADMIN');

INSERT INTO owners (name, password)
VALUES
    ('user', '$2a$04$laSFt3sYh79/B8B6JgshV.BmhwteQs2S6y0VLfAlkfaNC1kvpxj/6'),
    ('admin', '$2a$04$laSFt3sYh79/B8B6JgshV.BmhwteQs2S6y0VLfAlkfaNC1kvpxj/6');

INSERT INTO owners_roles (owner_id, role_id)
VALUES
    (1, 1),
    (2, 2);
