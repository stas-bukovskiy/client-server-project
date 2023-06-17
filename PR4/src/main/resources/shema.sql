CREATE TABLE IF NOT EXISTS good_group
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS good
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100)   NOT NULL,
    quantity INTEGER        NOT NULL DEFAULT 0,
    price    DECIMAL(10, 2) NOT NULL,
    group_id INTEGER        REFERENCES good_group (id) ON DELETE SET NULL
);