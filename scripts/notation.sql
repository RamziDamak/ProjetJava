CREATE TABLE IF NOT EXISTS notation (
    hotelid INT,
    securite INT,
    nourriture INT,
    proprete INT,
    chambre INT,
    emplacement INT,
    service INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (hotelid)
);