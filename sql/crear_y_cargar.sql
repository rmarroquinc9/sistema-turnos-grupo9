-- Habilitar claves foráneas
PRAGMA foreign_keys = ON;

-- Eliminar tablas si existen (para poder recrear desde cero)
DROP TABLE IF EXISTS turnos;
DROP TABLE IF EXISTS servicios;
DROP TABLE IF EXISTS clientes;

-- Tabla de clientes
CREATE TABLE clientes (
    codigo INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    telefono TEXT NOT NULL,
    estado TEXT NOT NULL CHECK (estado IN ('Activo', 'Inactivo'))
);

-- Tabla de servicios
CREATE TABLE servicios (
    codigo INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    estado TEXT NOT NULL CHECK (estado IN ('Activo', 'Inactivo'))
);

-- Tabla de turnos
CREATE TABLE turnos (
    numero INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_cliente INTEGER NOT NULL,
    codigo_servicio INTEGER NOT NULL,
    fecha_generacion TEXT NOT NULL,
    estado TEXT NOT NULL CHECK (estado IN ('PENDIENTE', 'ATENDIDO', 'CANCELADO')),
    fecha_atencion TEXT,
    FOREIGN KEY (codigo_cliente) REFERENCES clientes(codigo),
    FOREIGN KEY (codigo_servicio) REFERENCES servicios(codigo)
);

-- Datos iniciales de clientes
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (1001, 'Ana López', '55501001', 'Activo');
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (1002, 'Carlos Pérez', '55501002', 'Activo');
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (1003, 'María García', '55501003', 'Activo');
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (1004, 'José Hernández', '55501004', 'Activo');
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (1005, 'Luisa Morales', '55501005', 'Activo');

-- Datos iniciales de servicios
INSERT INTO servicios (codigo, nombre, estado) VALUES (1, 'Información', 'Activo');
INSERT INTO servicios (codigo, nombre, estado) VALUES (2, 'Entrega de documentos', 'Activo');
INSERT INTO servicios (codigo, nombre, estado) VALUES (3, 'Actualización de datos', 'Activo');