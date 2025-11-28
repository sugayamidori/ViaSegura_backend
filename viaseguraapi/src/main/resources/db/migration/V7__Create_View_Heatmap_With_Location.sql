CREATE VIEW heatmap_with_location AS
SELECT
    ha.id,
    ha.h3_cell,
    ha.year,
    ha.month,
    ha.num_sinistros,
    hs.avg_latitude,
    hs.avg_longitude,
    hs.bairro_clean,
    ha.created_at
FROM heatmap ha
LEFT JOIN h3_summary hs ON ha.h3_cell = hs.h3_cell;