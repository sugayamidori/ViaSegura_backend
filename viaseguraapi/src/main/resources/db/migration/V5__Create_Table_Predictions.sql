CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE predictions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    h3_cell VARCHAR(15) NOT NULL,
    week_start DATE NOT NULL,
    predicted_accidents DECIMAL(15, 10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(h3_cell, week_start)
);

CREATE INDEX idx_predictions_cell ON predictions(h3_cell);
CREATE INDEX idx_predictions_date ON predictions(week_start);