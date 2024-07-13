CREATE UNIQUE INDEX fk_users_email_username ON users(email, username);
CREATE UNIQUE INDEX fk_enrollments_user_id_course_id ON enrollments(user_id, course_id);
CREATE UNIQUE INDEX fk_password_reset_tokens_user_id ON password_reset_tokens(user_id);