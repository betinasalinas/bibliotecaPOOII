-- ============================================================
-- Sistema de Biblioteca
-- Script de creación de base de datos y tablas
-- ============================================================

CREATE DATABASE IF NOT EXISTS biblioteca_db;
USE biblioteca_db;

-- Usuarios del sistema (bibliotecarios y estudiantes que inician sesión)
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rut VARCHAR(15) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    rol ENUM('BIBLIOTECARIO','ESTUDIANTE') NOT NULL
);

-- Estudiantes registrados (vinculados a préstamos, no necesariamente a un login)
CREATE TABLE IF NOT EXISTS estudiantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rut VARCHAR(15) NOT NULL UNIQUE,
    curso VARCHAR(50),
    correo VARCHAR(100)
);

-- Categorías de libros
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Libros del inventario
CREATE TABLE IF NOT EXISTS libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    isbn VARCHAR(30),
    editorial VARCHAR(100),
    stock INT NOT NULL DEFAULT 0,
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);

-- Préstamos de libros a estudiantes
CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE NOT NULL,
    devuelto BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id),
    FOREIGN KEY (id_libro) REFERENCES libros(id)
);
