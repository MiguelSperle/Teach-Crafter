CREATE TABLE users (
   id VARCHAR(255) NOT NULL PRIMARY KEY,
   username VARCHAR(100) NOT NULL,
   role VARCHAR(50) NOT NULL,
   name VARCHAR(100) NOT NULL,
   email VARCHAR(255) NOT NULL,
   password_hash VARCHAR(255) NOT NULL,
   avatar TEXT NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);