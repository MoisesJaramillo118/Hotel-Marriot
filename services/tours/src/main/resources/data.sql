INSERT INTO tour (id, nombre, fecha, cupo_total, cupo_disponible, precio, moneda) VALUES
    ('TOU-012', 'City tour Lima Colonial',              '2026-11-02', 20, 20, 380.00, 'PEN'),
    ('TOU-013', 'Santuario de Pachacamac',              '2026-11-03', 15, 15, 450.00, 'PEN'),
    ('TOU-014', 'Islas Palomino: nado con lobos marinos','2026-11-05', 12, 12, 520.00, 'PEN'),
    ('TOU-015', 'Circuito Mágico del Agua (agotado)',   '2026-11-02', 10,  0, 120.00, 'PEN'),
    ('TOU-016', 'Ruta del Pisco en Lunahuaná',          '2026-12-12', 18, 18, 690.00, 'PEN')
ON CONFLICT (id) DO NOTHING;
