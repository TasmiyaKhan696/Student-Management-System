CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,

    student_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),

    department VARCHAR(100) NOT NULL,
    grade VARCHAR(10),

    enroll_date DATE,
    library_status VARCHAR(20) DEFAULT 'Not Borrowed',
    fees_paid BOOLEAN DEFAULT FALSE,
    sem1_marks INT DEFAULT 0,
    sem2_marks INT DEFAULT 0
);