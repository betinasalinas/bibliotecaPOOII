package vista;

import controlador.CategoriaController;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de gestión de Categorías (necesarias para clasificar los libros).
 */
public class VentanaCategorias extends JFrame {

    private final CategoriaController controller = new CategoriaController();

    private final JTextField txtNombre = new JTextField(20);
    private final DefaultTableModel modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public VentanaCategorias() {
        super("Biblioteca - Gestión de Categorías");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        add(panelForm, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar seleccionada");
        JButton btnEliminar = new JButton("Eliminar seleccionada");
        JButton btnRefrescar = new JButton("Refrescar");

        btnRegistrar.addActionListener(e -> registrar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefrescar.addActionListener(e -> refrescarTabla());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        refrescarTabla();
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
    }

    private void registrar() {
        Categoria categoria = controller.registrar(txtNombre.getText());
        if (categoria != null) {
            JOptionPane.showMessageDialog(this, "Categoría registrada correctamente.");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        boolean ok = controller.editar(id, txtNombre.getText());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada.");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean ok = controller.eliminar(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Categoría eliminada.");
                limpiar();
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede tener libros asociados).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        tabla.clearSelection();
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Categoria> lista = controller.listar();
        for (Categoria c : lista) {
            modeloTabla.addRow(new Object[]{c.getId(), c.getNombre()});
        }
    }
}
