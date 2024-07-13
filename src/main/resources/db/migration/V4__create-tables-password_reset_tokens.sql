CREATE TABLE password_reset_tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expires_in Date NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_password_reset_tokens_users_id FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);