-- Insert a few packages
INSERT INTO packages (id, name, country_code, total_credits, price, expiry_days)
VALUES
    (1, 'Basic Package SG', 'SG', 5, 10.0, 30),
    (2, 'Basic Package MM', 'MM', 5, 10.0, 30);

-- Insert a few schedules
INSERT INTO schedules (id, class_name, country_code, required_credits, max_slots, start_time, end_time)
VALUES
    (1, 'Yoga Class (SG)', 'SG', 1, 5, '2025-06-01T10:00:00', '2025-06-01T11:00:00'),
    (2, 'Pilates Class (MM)', 'MM', 1, 5, '2025-06-01T12:00:00', '2025-06-01T13:00:00');
