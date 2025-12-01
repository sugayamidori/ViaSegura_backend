CREATE VIEW h3_summary AS
SELECT
    h3_cell,
    COUNT(*) as num_coordenadas,
    AVG(latitude) as avg_latitude,
    AVG(longitude) as avg_longitude,
    MIN(bairro_clean) as bairro_clean,
    MIN(created_at) as first_seen,
    MAX(created_at) as last_seen
FROM h3_coordinates
GROUP BY h3_cell;