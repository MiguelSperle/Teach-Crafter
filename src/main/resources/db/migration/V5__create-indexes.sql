CREATE UNIQUE INDEX users_email_username_key ON users(email, username);
CREATE UNIQUE INDEX subscription_user_id_course_id_key ON subscription(user_id, course_id);
CREATE UNIQUE INDEX password_reset_token_user_id_key ON password_reset_token(user_id);
