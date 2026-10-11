-- ============================================================
-- Sistema de Biblioteca
-- Script de poblado de datos de ejemplo
-- Ejecutar DESPUÉS de Script_crea_tablas_biblioteca.sql
-- ============================================================

USE biblioteca_db;

-- Usuarios (1 bibliotecario + 1 estudiante de ejemplo para probar el login)
INSERT INTO usuarios (nombre, rut, correo, contrasena, rol) VALUES
('Ana Soto', '11111111-1', 'ana.soto@biblioteca.cl', '1234', 'BIBLIOTECARIO'),
('Pedro Ramírez', '22222222-2', 'pedro.ramirez@duoc.cl', '1234', 'ESTUDIANTE');

-- Estudiantes registrados para préstamos
INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES
('Pedro Ramírez', '22222222-2', 'DAO201', 'pedro.ramirez@duoc.cl'),
('María Fernández', '33333333-3', 'DAO201', 'maria.fernandez@duoc.cl'),
('Camila Torres', '44444444-4', 'DAO202', 'camila.torres@duoc.cl');

-- Categorías
INSERT INTO categorias (nombre) VALUES
('Programación'),
('Base de Datos'),
('Literatura'),
('Matemáticas');

-- Libros
INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES
('Java: Cómo Programar', 'Deitel & Deitel', '978-0134743356', 'Pearson', 5, 1),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Prentice Hall', 3, 1),
('Fundamentos de Bases de Datos', 'Silberschatz', '978-0073523323', 'McGraw-Hill', 4, 2),
('Cien Años de Soledad', 'Gabriel García Márquez', '978-0307474728', 'Sudamericana', 2, 3),
('Cálculo de una Variable', 'James Stewart', '978-1305270336', 'Cengage', 3, 4);

-- Préstamos de ejemplo (uno ya devuelto, otro pendiente)
INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES
(1, 1, '2026-09-20', '2026-09-27', TRUE),
(2, 3, '2026-10-05', '2026-10-12', FALSE);
