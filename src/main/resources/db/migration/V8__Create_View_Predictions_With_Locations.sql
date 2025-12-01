CREATE VIEW predictions_with_location AS
SELECT
    p.id,
    p.h3_cell,
    p.week_start,
    p.predicted_accidents,
    hs.avg_latitude,
    hs.avg_longitude,
    hs.bairro_clean,
    p.created_at
FROM predictions p
LEFT JOIN h3_summary hs ON p.h3_cell = hs.h3_cell;