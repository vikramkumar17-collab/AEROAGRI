MERGE INTO sensors (id, name, type, status) KEY (id) VALUES
 (1, 'LiDAR-01', 'LiDAR', 'ONLINE'),
 (2, 'GPS-01', 'GPS', 'ONLINE'),
 (3, 'IMU-01', 'IMU', 'ONLINE');
