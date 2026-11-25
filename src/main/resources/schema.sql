CREATE TABLE IF NOT EXISTS sensors (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    type VARCHAR(20),
    status VARCHAR(20)
);
CREATE TABLE IF NOT EXISTS sensor_readings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id INT,
    reading_value DOUBLE,
    details VARCHAR(200),
    unit VARCHAR(10),
    simulated BOOLEAN,
    mode VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS scans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    field_name VARCHAR(50),
    mode VARCHAR(10),
    status VARCHAR(20),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    overall_status VARCHAR(30),
    avg_height DOUBLE,
    warning VARCHAR(300)
);
CREATE TABLE IF NOT EXISTS scan_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    scan_id INT,
    sector VARCHAR(5),
    avg_height DOUBLE,
    problem_row INT,
    status VARCHAR(20),
    note VARCHAR(100)
);
