CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TABLE h3_coordinates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    h3_cell VARCHAR(15) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    bairro_clean VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_h3_cell ON h3_coordinates(h3_cell);
CREATE INDEX idx_bairro ON h3_coordinates(bairro_clean);

ALTER TABLE h3_coordinates
ADD CONSTRAINT unique_coordinate
UNIQUE (h3_cell, latitude, longitude, bairro_clean);