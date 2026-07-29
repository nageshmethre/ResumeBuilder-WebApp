-- Database Schema for Resume Builder Application
CREATE DATABASE IF NOT EXISTS resume_builder;
USE resume_builder;

-- Drop tables if they exist (ordered to prevent FK violations)
DROP TABLE IF EXISTS resume_references;
DROP TABLE IF EXISTS interests;
DROP TABLE IF EXISTS languages;
DROP TABLE IF EXISTS coding_profiles;
DROP TABLE IF EXISTS achievements;
DROP TABLE IF EXISTS workshops;
DROP TABLE IF EXISTS publications;
DROP TABLE IF EXISTS internships;
DROP TABLE IF EXISTS certifications;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS experience;
DROP TABLE IF EXISTS education;
DROP TABLE IF EXISTS resumes;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- 1. Roles table
CREATE TABLE roles (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL
);

-- 2. Users table
CREATE TABLE users (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(100) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. User-Roles Mapping table (Many-to-Many)
CREATE TABLE user_roles (
	user_id BIGINT NOT NULL,
	role_id BIGINT NOT NULL,
	PRIMARY KEY (user_id, role_id),
	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
	FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. Resumes table (One User can have Many Resumes, with layout customization options)
CREATE TABLE resumes (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT NOT NULL,
	title VARCHAR(255) NOT NULL,
	first_name VARCHAR(100) NOT NULL,
	last_name VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL,
	phone VARCHAR(30),
	address VARCHAR(255),
	summary TEXT,
	
	-- New Personal fields
	dob VARCHAR(30),
	city VARCHAR(100),
	state VARCHAR(100),
	country VARCHAR(100),
	linkedin VARCHAR(255),
	github VARCHAR(255),
	portfolio VARCHAR(255),
	website VARCHAR(255),

	-- Customization fields
	template VARCHAR(50) DEFAULT 'classic',
	font_family VARCHAR(50) DEFAULT 'Inter',
	font_size VARCHAR(20) DEFAULT 'medium',
	primary_color VARCHAR(30) DEFAULT '#4f46e5',
	line_spacing VARCHAR(20) DEFAULT 'normal',
	page_margins VARCHAR(20) DEFAULT 'normal',
	page_size VARCHAR(20) DEFAULT 'a4',
	show_sections TEXT, -- Comma-separated list of visible sections
	section_order TEXT, -- Comma-separated list of section ordering

	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Education table (One Resume can have Many Education entries)
CREATE TABLE education (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	institution VARCHAR(255) NOT NULL,
	degree VARCHAR(100) NOT NULL,
	field_of_study VARCHAR(100),
	start_date VARCHAR(30),
	end_date VARCHAR(30),
	description TEXT,
	
	-- New Education fields
	university VARCHAR(255),
	cgpa VARCHAR(20),
	percentage VARCHAR(20),
	location VARCHAR(100),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 6. Experience table (One Resume can have Many Experience entries)
CREATE TABLE experience (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	company VARCHAR(255) NOT NULL,
	position VARCHAR(100) NOT NULL,
	start_date VARCHAR(30),
	end_date VARCHAR(30),
	description TEXT,
	
	-- New Experience fields
	employment_type VARCHAR(50),
	location VARCHAR(100),
	is_current BOOLEAN DEFAULT FALSE,
	responsibilities TEXT,
	achievements TEXT,
	technologies VARCHAR(255),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 7. Skills table (One Resume can have Many Skills)
CREATE TABLE skills (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(100) NOT NULL,
	level VARCHAR(50), -- E.g. Beginner, Intermediate, Expert
	category VARCHAR(100), -- E.g. Programming Languages, Frameworks, Tools
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 8. Projects table (One Resume can have Many Projects)
CREATE TABLE projects (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	title VARCHAR(255) NOT NULL,
	description TEXT,
	technologies VARCHAR(255),
	link VARCHAR(255),
	
	-- New Project fields
	github_link VARCHAR(255),
	demo_link VARCHAR(255),
	role VARCHAR(100),
	team_size VARCHAR(20),
	duration VARCHAR(50),
	features TEXT,
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 9. Certifications table (One Resume can have Many Certifications)
CREATE TABLE certifications (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(255) NOT NULL,
	organization VARCHAR(255) NOT NULL,
	issue_date VARCHAR(30),
	expiry_date VARCHAR(30),
	credential_id VARCHAR(100),
	credential_url VARCHAR(255),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 10. Internships table (One Resume can have Many Internships)
CREATE TABLE internships (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	company VARCHAR(255) NOT NULL,
	position VARCHAR(255) NOT NULL,
	duration VARCHAR(100),
	description TEXT,
	technologies VARCHAR(255),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 11. Research Publications table (One Resume can have Many Publications)
CREATE TABLE publications (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	title VARCHAR(255) NOT NULL,
	publisher VARCHAR(255),
	doi VARCHAR(100),
	link VARCHAR(255),
	description TEXT,
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 12. Workshops table (One Resume can have Many Workshops)
CREATE TABLE workshops (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(255) NOT NULL,
	organization VARCHAR(255) NOT NULL,
	date VARCHAR(30),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 13. Achievements table (One Resume can have Many Achievements)
CREATE TABLE achievements (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	category VARCHAR(100) NOT NULL, -- Coding Contest, Hackathon, Award, etc.
	description TEXT NOT NULL,
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 14. Coding Profiles table (One Resume can have Many Coding Profiles)
CREATE TABLE coding_profiles (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	platform VARCHAR(100) NOT NULL, -- LeetCode, HackerRank, CodeChef, Codeforces, GeeksforGeeks, GitHub
	url VARCHAR(255) NOT NULL,
	rating VARCHAR(50),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 15. Languages table (One Resume can have Many Languages)
CREATE TABLE languages (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(100) NOT NULL,
	reading VARCHAR(50),
	writing VARCHAR(50),
	speaking VARCHAR(50),
	level VARCHAR(50), -- Beginner, Intermediate, Fluent, Native
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 16. Interests table (One Resume can have Many Interests)
CREATE TABLE interests (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(100) NOT NULL,
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 17. References table (One Resume can have Many References)
CREATE TABLE resume_references (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resume_id BIGINT NOT NULL,
	name VARCHAR(255) NOT NULL,
	relationship VARCHAR(255),
	email VARCHAR(100),
	phone VARCHAR(30),
	company VARCHAR(255),
	FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- Seed basic roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
