package vista;

import controlador.EstudianteController;
import controlador.PrestamoController;
import modelo.Estudiante;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de Reportes: libros más prestados, historial por estudiante,
 * y listado de préstamos actualmente vigentes (no devueltos).
 */
public class VentanaReportes extends JFrame {

    private final PrestamoController prestamoController = new PrestamoController();
    private final EstudianteController estudianteController = new EstudianteController();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(new Object[]{}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();

    public VentanaReportes() {
        super("Biblioteca - Reportes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnMasPrestados = new JButton("Libros más prestados");
        JButton btnVigentes = new JButton("Préstamos vigentes");
        JButton btnHistorial = new JButton("Ver historial del estudiante");

        btnMasPrestados.addActionListener(e -> mostrarLibrosMasPrestados());
        btnVigentes.addActionListener(e -> mostrarPrestamosVigentes());
        btnHistorial.addActionListener(e -> mostrarHistorialEstudiante());

        panelSuperior.add(btnMasPrestados);
        panelSuperior.add(btnVigentes);
        panelSuperior.add(new JLabel("Estudiante:"));
        panelSuperior.add(comboEstudiante);
        panelSuperior.add(btnHistorial);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        refrescarComboEstudiantes();
        mostrarPrestamosVigentes(); // reporte por defecto al abrir
    }

    private void refrescarComboEstudiantes() {
        comboEstudiante.removeAllItems();
        for (Estudiante e : estudianteController.listar()) comboEstudiante.addItem(e);
    }

    private void mostrarLibrosMasPrestados() {
        modeloTabla.setDataVector(new Object[][]{}, new Object[]{"Título", "Veces prestado"});
        List<Object[]> lista = prestamoController.librosMasPrestados();
        for (Object[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    private void mostrarPrestamosVigentes() {
        modeloTabla.setDataVector(new Object[][]{},
                new Object[]{"ID", "Estudiante", "Libro", "F. Préstamo", "F. Vencimiento", "Atrasado"});
        List<Prestamo> lista = prestamoController.listarVigentes();
        for (Prestamo p : lista) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.estaAtrasado()
            });
        }
    }

    private void mostrarHistorialEstudiante() {
        Estudiante estudiante = (Estudiante) comboEstudiante.getSelectedItem();
        if (estudiante == null) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla.setDataVector(new Object[][]{},
                new Object[]{"ID", "Libro", "F. Préstamo", "F. Devolución", "Devuelto"});
        List<Prestamo> lista = prestamoController.historialPorEstudiante(estudiante.getId());
        for (Prestamo p : lista) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getTituloLibro(), p.getFechaPrestamo(), p.getFechaDevolucion(), p.isDevuelto()
            });
        }
    }
}
