package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal de navegación. Las opciones disponibles dependen del rol
 * del usuario autenticado:
 * - BIBLIOTECARIO: gestión total (libros, categorías, estudiantes, préstamos, reportes).
 * - ESTUDIANTE: solo préstamo, devolución y consulta (reportes de solo lectura).
 */
public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;

    public VentanaPrincipal(Usuario usuario) {
        super("Biblioteca - Panel principal (" + usuario.getNombre() + ")");
        this.usuario = usuario;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 1, 10, 10));

        JLabel lblBienvenida = new JLabel("Sesión: " + usuario.getNombre() + " — Rol: " + usuario.getRol(), SwingConstants.CENTER);
        add(lblBienvenida);

        if (usuario.esBibliotecario()) {
            JButton btnLibros = new JButton("Gestión de Libros");
            JButton btnCategorias = new JButton("Gestión de Categorías");
            JButton btnEstudiantes = new JButton("Gestión de Estudiantes");
            JButton btnPrestamos = new JButton("Préstamos y Devoluciones");
            JButton btnReportes = new JButton("Reportes");
            JButton btnSalir = new JButton("Cerrar sesión");

            btnLibros.addActionListener(e -> new VentanaLibros().setVisible(true));
            btnCategorias.addActionListener(e -> new VentanaCategorias().setVisible(true));
            btnEstudiantes.addActionListener(e -> new VentanaEstudiantes().setVisible(true));
            btnPrestamos.addActionListener(e -> new VentanaPrestamos(usuario).setVisible(true));
            btnReportes.addActionListener(e -> new VentanaReportes().setVisible(true));
            btnSalir.addActionListener(e -> cerrarSesion());

            add(btnLibros);
            add(btnCategorias);
            add(btnEstudiantes);
            add(btnPrestamos);
            add(btnReportes);
            add(btnSalir);
        } else {
            // Rol ESTUDIANTE: solo préstamo, devolución y consulta
            JButton btnPrestamos = new JButton("Préstamos y Devoluciones");
            JButton btnReportes = new JButton("Consultar Reportes");
            JButton btnSalir = new JButton("Cerrar sesión");

            btnPrestamos.addActionListener(e -> new VentanaPrestamos(usuario).setVisible(true));
            btnReportes.addActionListener(e -> new VentanaReportes().setVisible(true));
            btnSalir.addActionListener(e -> cerrarSesion());

            add(btnPrestamos);
            add(btnReportes);
            add(btnSalir);
        }
    }

    private void cerrarSesion() {
        new VentanaLogin().setVisible(true);
        dispose();
    }
}
