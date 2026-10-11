package vista;

import controlador.EstudianteController;
import controlador.LibroController;
import controlador.PrestamoController;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de Préstamos y Devoluciones.
 *
 * El registro de un préstamo se ejecuta en un hilo en segundo plano
 * (ver PrestamoController.registrarPrestamoEnSegundoPlano): mientras el
 * hilo descuenta el stock (de forma sincronizada) y guarda el préstamo,
 * la interfaz sigue respondiendo con normalidad; el botón "Registrar
 * préstamo" se deshabilita brevemente para evitar doble clic, y los
 * resultados se muestran a través de SwingUtilities.invokeLater().
 */
public class VentanaPrestamos extends JFrame {

    private final PrestamoController prestamoController = new PrestamoController();
    private final EstudianteController estudianteController = new EstudianteController();
    private final LibroController libroController = new LibroController();

    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();
    private final JComboBox<Libro> comboLibro = new JComboBox<>();
    private final JTextField txtDiasPlazo = new JTextField("7", 4);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Estudiante", "Libro", "F. Préstamo", "F. Devolución", "Devuelto", "Atrasado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JButton btnRegistrar = new JButton("Registrar préstamo");

    public VentanaPrestamos(Usuario usuario) {
        super("Biblioteca - Préstamos y Devoluciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Estudiante:"));
        panelForm.add(comboEstudiante);
        panelForm.add(new JLabel("Libro:"));
        panelForm.add(comboLibro);
        panelForm.add(new JLabel("Días de plazo:"));
        panelForm.add(txtDiasPlazo);
        add(panelForm, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnDevolver = new JButton("Registrar devolución");
        JButton btnRefrescar = new JButton("Refrescar");

        btnRegistrar.addActionListener(e -> registrarPrestamo());
        btnDevolver.addActionListener(e -> registrarDevolucion());
        btnRefrescar.addActionListener(e -> {
            refrescarCombos();
            refrescarTabla();
        });

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnDevolver);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        refrescarCombos();
        refrescarTabla();
    }

    private void registrarPrestamo() {
        Estudiante estudiante = (Estudiante) comboEstudiante.getSelectedItem();
        Libro libro = (Libro) comboLibro.getSelectedItem();

        if (estudiante == null || libro == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes tener al menos un estudiante y un libro registrados.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int diasPlazo;
        try {
            diasPlazo = Integer.parseInt(txtDiasPlazo.getText().trim());
        } catch (NumberFormatException ex) {
            diasPlazo = PrestamoController.DIAS_PLAZO_DEFECTO;
        }

        // Se deshabilita el botón para evitar que el usuario haga doble clic
        // mientras el hilo en segundo plano está trabajando.
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        prestamoController.registrarPrestamoEnSegundoPlano(estudiante.getId(), libro.getId(), diasPlazo,
                new PrestamoController.PrestamoCallback() {
                    @Override
                    public void onExito(Prestamo prestamo) {
                        // Este método corre en el hilo en segundo plano:
                        // hay que volver al hilo de Swing para tocar la interfaz.
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(VentanaPrestamos.this,
                                    "Préstamo registrado correctamente (ID " + prestamo.getId() + ").");
                            btnRegistrar.setEnabled(true);
                            btnRegistrar.setText("Registrar préstamo");
                            refrescarCombos();
                            refrescarTabla();
                        });
                    }

                    @Override
                    public void onError(String mensaje) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(VentanaPrestamos.this, mensaje,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            btnRegistrar.setEnabled(true);
                            btnRegistrar.setText("Registrar préstamo");
                        });
                    }
                });
    }

    private void registrarDevolucion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un préstamo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean yaDevuelto = (boolean) modeloTabla.getValueAt(fila, 5);
        if (yaDevuelto) {
            JOptionPane.showMessageDialog(this, "Ese préstamo ya fue devuelto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        List<Prestamo> todos = prestamoController.listar();
        Prestamo prestamo = todos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);

        if (prestamo == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = prestamoController.registrarDevolucion(prestamo);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Devolución registrada. Se repuso el stock del libro.");
            refrescarCombos();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescarCombos() {
        comboEstudiante.removeAllItems();
        for (Estudiante e : estudianteController.listar()) comboEstudiante.addItem(e);

        comboLibro.removeAllItems();
        for (Libro l : libroController.listar()) comboLibro.addItem(l);
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Prestamo p : prestamoController.listar()) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.isDevuelto(), p.estaAtrasado()
            });
        }
    }
}
