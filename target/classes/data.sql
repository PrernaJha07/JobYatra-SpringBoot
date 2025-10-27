-- Insert sample admin user
INSERT IGNORE INTO users (full_name, email, password, address, phone, is_admin, created_at) 
VALUES ('Admin User', 'abhi@gmail.com', '123456', 'Admin Address', '+90658566693', TRUE, NOW());

-- Insert sample jobs
INSERT IGNORE INTO jobs (title, description, company, location, salary, job_type, created_at, active) VALUES
('Senior Software Engineer', 'Develop and maintain scalable web applications using modern technologies. Work with cross-functional teams to deliver high-quality software solutions.', 'Google', 'Mountain View, CA', '$120,000 - $150,000', 'Full-time', NOW(), TRUE),
('Frontend Developer', 'Create responsive and interactive user interfaces using React and Vue.js. Collaborate with UX designers to implement beautiful user experiences.', 'Microsoft', 'Seattle, WA', '$100,000 - $130,000', 'Full-time', NOW(), TRUE),
('Data Scientist', 'Analyze complex datasets and build machine learning models. Work with big data technologies to derive actionable insights for business decisions.', 'Amazon', 'New York, NY', '$130,000 - $160,000', 'Full-time', NOW(), TRUE),
('Product Manager', 'Lead product development from conception to launch. Define product strategy and work with engineering teams to deliver exceptional products.', 'Apple', 'Cupertino, CA', '$140,000 - $170,000', 'Full-time', NOW(), TRUE),
('DevOps Engineer', 'Manage cloud infrastructure and deployment pipelines. Implement CI/CD processes and ensure system reliability and scalability.', 'Netflix', 'Remote', '$110,000 - $140,000', 'Remote', NOW(), TRUE),
('UX Designer', 'Design user-centered interfaces and experiences. Conduct user research and create wireframes and prototypes for web and mobile applications.', 'Facebook', 'Menlo Park, CA', '$90,000 - $120,000', 'Full-time', NOW(), TRUE);


CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    text TEXT NOT NULL,
    photo_path VARCHAR(500),
    reply TEXT,
    replied_by BIGINT,
    replied_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for better performance
CREATE INDEX idx_feedback_user_id ON feedback(user_id);
CREATE INDEX idx_feedback_created_at ON feedback(created_at);
CREATE INDEX idx_feedback_read_status ON feedback(is_read);