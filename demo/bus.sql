
CREATE DATABASE bus_reservation;
USE bus_reservation;


CREATE TABLE bus (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    available_seats INT NOT NULL,
    total_seats INT NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL
);