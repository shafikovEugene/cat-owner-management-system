CREATE TABLE IF NOT EXISTS owners (
    owner_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birth_date DATE
);

CREATE TABLE IF NOT EXISTS cats (
    cat_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    breed VARCHAR(100),
    color VARCHAR(100),
    owner_id INT REFERENCES owners(owner_id)
);

CREATE TABLE IF NOT EXISTS cat_friendships (
    cat_id INT REFERENCES cats(cat_id),
    friend_cat_id INT REFERENCES cats(cat_id),
    PRIMARY KEY (cat_id, friend_cat_id)
);
