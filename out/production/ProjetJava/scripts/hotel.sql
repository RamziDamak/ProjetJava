USE projet_java;

-- Create the hotel table
CREATE TABLE IF NOT EXISTS hotel (
    hotelid INT AUTO_INCREMENT PRIMARY KEY,
    nomhotel VARCHAR(255) NOT NULL
);

-- Insert hotel names with specific IDs
INSERT INTO hotel (hotelid, nomhotel) VALUES
(1, 'Azure Heights Inn'),
(2, 'Serenity Suites'),
(3, 'Harmony Haven Hotel'),
(4, 'Celestial Retreat'),
(5, 'Tranquil Oasis Lodge'),
(6, 'Majestic Horizon Resort'),
(7, 'Velvet Vista Inn'),
(8, 'Radiant Residences'),
(9, 'Pacific Breeze Hotel'),
(10, 'Enchanting Elegance Suites');
