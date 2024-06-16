CREATE UNIQUE INDEX users_email_username_key ON users(email, username);
CREATE UNIQUE INDEX enrollments_user_id_course_id_key ON enrollments(user_id, course_id);
CREATE UNIQUE INDEX password_reset_tokens_user_id_key ON password_reset_tokens(user_id);