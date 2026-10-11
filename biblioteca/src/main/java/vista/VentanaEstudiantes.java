package vista;

import controlador.EstudianteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de gestión de Estudiantes (CRUD).
 */
public class VentanaEstudiantes extends JFrame {

    private final EstudianteController controller = new EstudianteController();

    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtRut = new JTextField(10);
    private final JTextField txtCurso = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(15);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public VentanaEstudiantes() {
        super("Biblioteca - Gestión de Estudiantes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("RUT:"));
        panelForm.add(txtRut);
        panelForm.add(new JLabel("Curso:"));
        panelForm.add(txtCurso);
        panelForm.add(new JLabel("Correo:"));
        panelForm.add(txtCorreo);
        add(panelForm, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar seleccionado");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
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
        Estudiante estudiante = controller.registrar(txtNombre.getText(), txtRut.getText(), txtCurso.getText(), txtCorreo.getText());
        if (estudiante != null) {
            JOptionPane.showMessageDialog(this, "Estudiante registrado correctamente (ID " + estudiante.getId() + ").");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Verifica que nombre y RUT estén completos.", "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        boolean ok = controller.editar(id, txtNombre.getText(), txtRut.getText(), txtCurso.getText(), txtCorreo.getText());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Estudiante actualizado.");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar al estudiante #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean ok = controller.eliminar(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
                limpiar();
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede tener préstamos asociados).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            txtRut.setText((String) modeloTabla.getValueAt(fila, 2));
            txtCurso.setText((String) modeloTabla.getValueAt(fila, 3));
            txtCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Estudiante> lista = controller.listar();
        for (Estudiante e : lista) {
            modeloTabla.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
        }
    }
}
