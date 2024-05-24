CREATE TABLE courses_contents (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    video_url TEXT,
    status VARCHAR(15) NOT NULL,
    release_date DATE NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    course_module VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT course_id_key FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE
);