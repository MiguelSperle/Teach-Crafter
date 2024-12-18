CREATE TABLE courses_contents (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    video_url TEXT,
    status VARCHAR(15) NOT NULL,
    release_date DATE NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    content_module VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_contents_courses_id FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE
);