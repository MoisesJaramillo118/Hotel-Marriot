INSERT INTO paquete (id, nombre, descripcion, precio, moneda, activo) VALUES
    ('PAQ-001', 'Lima Gastronómica', 'Cena de degustación en el hotel, clase de cocina peruana y visita al mercado de Surquillo.', 1200.00, 'PEN', true),
    ('PAQ-002', 'Lima Colonial y Barranco', 'Centro histórico, catacumbas de San Francisco y atardecer en Barranco con traslados.', 850.00, 'PEN', true),
    ('PAQ-003', 'Escapada a Paracas', 'Dos días en Paracas: Islas Ballestas, reserva nacional y traslado ida y vuelta desde el hotel.', 1650.00, 'PEN', true),
    ('PAQ-004', 'Spa y Relax (temporada pasada)', 'Paquete de spa fuera de catálogo.', 600.00, 'PEN', false)
ON CONFLICT (id) DO NOTHING;
