-- DROP TABLE IF EXISTS activity_logs;
-- DROP TABLE IF EXISTS contacts;
-- DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'DEACTIVATED', 'SUSPENDED') DEFAULT 'ACTIVE',
    deactivated_at TIMESTAMP DEFAULT NULL,
    deactivated_by VARCHAR(255) DEFAULT NULL
);

CREATE TABLE contacts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255) DEFAULT NULL,
    address TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE activity_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    action ENUM('GET','CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



-- Inserting Users
-- INSERT INTO users (id, username, email, password, name, role, status)
-- VALUES
-- ('user1', 'john_doe', 'john.doe@example.com', 'password123', 'John Doe', 'USER', 'ACTIVE'),
-- ('user2', 'jane_doe', 'jane.doe@example.com', 'password123', 'Jane Doe', 'USER', 'ACTIVE'),
-- ('user3', 'alice_smith', 'alice.smith@example.com', 'password123', 'Alice Smith', 'USER', 'ACTIVE'),
-- ('user4', 'bob_jones', 'bob.jones@example.com', 'password123', 'Bob Jones', 'USER', 'ACTIVE');

-- -- Inserting Contacts for user1
-- INSERT INTO contacts (user_id, name, phone, email, address)
-- VALUES
-- ('user1', 'Mike Johnson', '555-0101', 'mike.johnson@example.com', '123 Main St, City'),
-- ('user1', 'Sara Lee', '555-0102', 'sara.lee@example.com', '124 Main St, City'),
-- ('user1', 'Tom White', '555-0103', 'tom.white@example.com', '125 Main St, City'),
-- ('user1', 'Nina Brown', '555-0104', 'nina.brown@example.com', '126 Main St, City'),
-- ('user1', 'James Green', '555-0105', 'james.green@example.com', '127 Main St, City');

-- -- Inserting Contacts for user2
-- INSERT INTO contacts (user_id, name, phone, email, address)
-- VALUES
-- ('user2', 'Lilly Gray', '555-0201', 'lilly.gray@example.com', '201 Oak St, City'),
-- ('user2', 'Chris Blue', '555-0202', 'chris.blue@example.com', '202 Oak St, City'),
-- ('user2', 'Patty Black', '555-0203', 'patty.black@example.com', '203 Oak St, City'),
-- ('user2', 'Rachel White', '555-0204', 'rachel.white@example.com', '204 Oak St, City'),
-- ('user2', 'Oscar Red', '555-0205', 'oscar.red@example.com', '205 Oak St, City');

-- -- Inserting Contacts for user3
-- INSERT INTO contacts (user_id, name, phone, email, address)
-- VALUES
-- ('user3', 'Nancy Yellow', '555-0301', 'nancy.yellow@example.com', '301 Pine St, City'),
-- ('user3', 'Bruce Pink', '555-0302', 'bruce.pink@example.com', '302 Pine St, City'),
-- ('user3', 'Wendy Purple', '555-0303', 'wendy.purple@example.com', '303 Pine St, City'),
-- ('user3', 'Steve Gold', '555-0304', 'steve.gold@example.com', '304 Pine St, City'),
-- ('user3', 'Gary Silver', '555-0305', 'gary.silver@example.com', '305 Pine St, City');

-- -- Inserting Contacts for user4
-- INSERT INTO contacts (user_id, name, phone, email, address)
-- VALUES
-- ('user4', 'Megan White', '555-0401', 'megan.white@example.com', '401 Birch St, City'),
-- ('user4', 'David Blue', '555-0402', 'david.blue@example.com', '402 Birch St, City'),
-- ('user4', 'Helen Brown', '555-0403', 'helen.brown@example.com', '403 Birch St, City'),
-- ('user4', 'Oscar Green', '555-0404', 'oscar.green@example.com', '404 Birch St, City'),
-- ('user4', 'Paul Red', '555-0405', 'paul.red@example.com', '405 Birch St, City');



-- select * from users;
-- select * from contacts;

-- select * from activity_logs;