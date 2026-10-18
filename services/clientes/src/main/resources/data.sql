-- Cliente de ejemplo del contrato (Entregable 2, sección 3.1).
INSERT INTO cliente (id, nombres, apellidos, email, doc_tipo, doc_numero, telefono, estado, creado_en)
VALUES ('CLI-000123', 'Mario Iván', 'Aponte Matos', 'mario@example.com', 'DNI', '70000000', '+51 999888777',
        'ACTIVO', '2026-10-07T09:00:00-05:00')
ON CONFLICT (id) DO NOTHING;
