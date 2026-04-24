-- ============================================================
--  RIWI Talent Manager — Schema MySQL
--  Ejecutar antes de correr la aplicación:
--    mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS riwi_talent
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE riwi_talent;

-- ── Tabla de Coders (Empleados) ──────────────────────────────
CREATE TABLE IF NOT EXISTS empleados (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    apellido      VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    cohorte       VARCHAR(50)   NOT NULL,
    stack         VARCHAR(80)   NOT NULL,
    activo        TINYINT(1)    NOT NULL DEFAULT 1,
    fecha_ingreso DATE          NOT NULL DEFAULT (CURRENT_DATE),
    INDEX idx_email   (email),
    INDEX idx_cohorte (cohorte)
);

-- ── Datos de ejemplo ─────────────────────────────────────────
INSERT INTO empleados (nombre, apellido, email, cohorte, stack) VALUES
('Jeronimo', 'López',    'Jeronimo.lopez@riwi.io',    'Cohorte-12', 'Java Full Stack'),
('Juliana', 'Gómez',    'Juliana.gomez@riwi.io',    'Cohorte-12', 'Node.js'),
('Lucas',    'Martínez', 'Lucas.martinez@riwi.io',    'Cohorte-11', 'PHP Laravel');

-- ── PostgreSQL (referencia, descomentar si migras) ───────────
-- CREATE TABLE IF NOT EXISTS empleados (
--     id            SERIAL PRIMARY KEY,
--     nombre        VARCHAR(100)  NOT NULL,
--     apellido      VARCHAR(100)  NOT NULL,
--     email         VARCHAR(150)  NOT NULL UNIQUE,
--     cohorte       VARCHAR(50)   NOT NULL,
--     stack         VARCHAR(80)   NOT NULL,
--     activo        BOOLEAN       NOT NULL DEFAULT TRUE,
--     fecha_ingreso DATE          NOT NULL DEFAULT CURRENT_DATE
-- );
