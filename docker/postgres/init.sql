-- Un solo motor PostgreSQL con una base independiente por servicio (database-per-service).
-- No hay claves foráneas entre bases: las relaciones son referencias lógicas por id.
CREATE DATABASE db_clientes;
CREATE DATABASE db_habitaciones;
CREATE DATABASE db_reservas;
CREATE DATABASE db_paquetes;
CREATE DATABASE db_tours;
CREATE DATABASE db_pagos;
CREATE DATABASE db_notificaciones;
