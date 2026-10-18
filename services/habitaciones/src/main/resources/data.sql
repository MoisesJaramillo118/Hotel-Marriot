-- Inventario inicial de ejemplo (tarifas en soles por noche).
INSERT INTO habitacion (id, tipo, piso, tarifa_noche, moneda, estado) VALUES
    ('HAB-101', 'SIMPLE',       1,  420.00, 'PEN', 'DISPONIBLE'),
    ('HAB-102', 'SIMPLE',       1,  420.00, 'PEN', 'DISPONIBLE'),
    ('HAB-201', 'DOBLE',        2,  580.00, 'PEN', 'DISPONIBLE'),
    ('HAB-202', 'DOBLE',        2,  580.00, 'PEN', 'DISPONIBLE'),
    ('HAB-205', 'DOBLE',        2,  610.00, 'PEN', 'MANTENIMIENTO'),
    ('HAB-301', 'SUITE',        3,  920.00, 'PEN', 'DISPONIBLE'),
    ('HAB-305', 'SUITE',        3,  920.00, 'PEN', 'DISPONIBLE'),
    ('HAB-410', 'SUITE',        4,  950.00, 'PEN', 'DISPONIBLE'),
    ('HAB-501', 'PRESIDENCIAL', 5, 2400.00, 'PEN', 'DISPONIBLE')
ON CONFLICT (id) DO NOTHING;
