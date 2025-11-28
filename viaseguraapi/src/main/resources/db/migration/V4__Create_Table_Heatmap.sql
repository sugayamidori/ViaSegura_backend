CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE heatmap (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    h3_cell VARCHAR(15) NOT NULL,
    year INT NOT NULL,
    month INT NOT NULL,
    num_sinistros DECIMAL(10, 2) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(h3_cell, year, month)
);

CREATE INDEX idx_heatmap_cell ON heatmap(h3_cell);
CREATE INDEX idx_heatmap_date ON heatmap(year, month);