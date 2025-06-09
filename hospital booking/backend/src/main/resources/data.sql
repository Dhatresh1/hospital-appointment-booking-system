-- Roles
INSERT INTO roles(name) VALUES('ROLE_PATIENT');
INSERT INTO roles(name) VALUES('ROLE_DOCTOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

-- Specializations
INSERT INTO specializations(name) VALUES('Cardiology');
INSERT INTO specializations(name) VALUES('Neurology');
INSERT INTO specializations(name) VALUES('Orthopedics');
INSERT INTO specializations(name) VALUES('Pediatrics');

-- Admin User
INSERT INTO users(name, username, email, password) VALUES('Admin', 'admin', 'admin@hospital.com', '$2a$10$5PiyN0MsG0y886d8B6Bu/.z6Z7nS8A3YQu7TXM7Zgw8nZHCiQY1bK');
INSERT INTO user_roles(user_id, role_id) VALUES(1, 3);

-- Sample Doctors
INSERT INTO users(name, username, email, password) VALUES('Dr. John Smith', 'drjohn', 'john@hospital.com', '$2a$10$5PiyN0MsG0y886d8B6Bu/.z6Z7nS8A3YQu7TXM7Zgw8nZHCiQY1bK');
INSERT INTO doctors(user_id, specialization_id) VALUES(2, 1);
INSERT INTO user_roles(user_id, role_id) VALUES(2, 2);

INSERT INTO users(name, username, email, password) VALUES('Dr. Sarah Johnson', 'drsarah', 'sarah@hospital.com', '$2a$10$5PiyN0MsG0y886d8B6Bu/.z6Z7nS8A3YQu7TXM7Zgw8nZHCiQY1bK');
INSERT INTO doctors(user_id, specialization_id) VALUES(3, 2);
INSERT INTO user_roles(user_id, role_id) VALUES(3, 2);

-- Sample Patients
INSERT INTO users(name, username, email, password) VALUES('Patient One', 'patient1', 'patient1@example.com', '$2a$10$5PiyN0MsG0y886d8B6Bu/.z6Z7nS8A3YQu7TXM7Zgw8nZHCiQY1bK');
INSERT INTO patients(user_id, date_of_birth, phone) VALUES(4, '1985-05-15', '1234567890');
INSERT INTO user_roles(user_id, role_id) VALUES(4, 1);

INSERT INTO users(name, username, email, password) VALUES('Patient Two', 'patient2', 'patient2@example.com', '$2a$10$5PiyN0MsG0y886d8B6Bu/.z6Z7nS8A3YQu7TXM7Zgw8nZHCiQY1bK');
INSERT INTO patients(user_id, date_of_birth, phone) VALUES(5, '1990-08-22', '9876543210');
INSERT INTO user_roles(user_id, role_id) VALUES(5, 1);