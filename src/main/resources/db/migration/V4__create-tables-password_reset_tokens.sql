CREATE TABLE password_reset_tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expires_in Date NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    CONSTRAINT user_id_key FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);