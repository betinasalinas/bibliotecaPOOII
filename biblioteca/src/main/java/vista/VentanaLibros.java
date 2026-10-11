package vista;

import controlador.CategoriaController;
import controlador.LibroController;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de gestión de Libros (CRUD). La categoría se selecciona desde un
 * JComboBox cargado desde la base de datos (muestra "id - nombre" pero
 * guarda internamente el id).
 */
public class VentanaLibros extends JFrame {

    private final LibroController libroController = new LibroController();
    private final CategoriaController categoriaController = new CategoriaController();

    private final JTextField txtTitulo = new JTextField(15);
    private final JTextField txtAutor = new JTextField(12);
    private final JTextField txtIsbn = new JTextField(10);
    private final JTextField txtEditorial = new JTextField(10);
    private final JTextField txtStock = new JTextField(4);
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public VentanaLibros() {
        super("Biblioteca - Gestión de Libros");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Título:"));
        panelForm.add(txtTitulo);
        panelForm.add(new JLabel("Autor:"));
        panelForm.add(txtAutor);
        panelForm.add(new JLabel("ISBN:"));
        panelForm.add(txtIsbn);
        panelForm.add(new JLabel("Editorial:"));
        panelForm.add(txtEditorial);
        panelForm.add(new JLabel("Stock:"));
        panelForm.add(txtStock);
        panelForm.add(new JLabel("Categoría:"));
        panelForm.add(comboCategoria);
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
        btnRefrescar.addActionListener(e -> {
            refrescarCombo();
            refrescarTabla();
        });

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        refrescarCombo();
        refrescarTabla();
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
    }

    private void registrar() {
        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Primero crea al menos una categoría.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Libro libro = libroController.registrar(txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(),
                txtEditorial.getText(), txtStock.getText(), categoriaSeleccionada.getId());

        if (libro != null) {
            JOptionPane.showMessageDialog(this, "Libro registrado correctamente (ID " + libro.getId() + ").");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Verifica título, autor y que el stock sea un número válido.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = libroController.editar(id, txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(),
                txtEditorial.getText(), txtStock.getText(), categoriaSeleccionada.getId());

        if (ok) {
            JOptionPane.showMessageDialog(this, "Libro actualizado.");
            limpiar();
            refrescarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar. Verifica los datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean ok = libroController.eliminar(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Libro eliminado.");
                limpiar();
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede tener préstamos asociados).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        txtTitulo.setText((String) modeloTabla.getValueAt(fila, 1));
        txtAutor.setText((String) modeloTabla.getValueAt(fila, 2));
        txtIsbn.setText((String) modeloTabla.getValueAt(fila, 3));
        txtEditorial.setText((String) modeloTabla.getValueAt(fila, 4));
        txtStock.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));

        String categoriaTexto = (String) modeloTabla.getValueAt(fila, 6);
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).toString().equals(categoriaTexto)) {
                comboCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiar() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        tabla.clearSelection();
    }

    public void refrescarCombo() {
        comboCategoria.removeAllItems();
        List<Categoria> categorias = categoriaController.listar();
        for (Categoria c : categorias) {
            comboCategoria.addItem(c);
        }
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Libro> lista = libroController.listar();
        for (Libro l : lista) {
            modeloTabla.addRow(new Object[]{
                    l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(),
                    l.getStock(), l.getIdCategoria() + " - " + l.getNombreCategoria()
            });
        }
    }
}
