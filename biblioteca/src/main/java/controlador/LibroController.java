package controlador;

import dao.LibroDAO;
import modelo.Libro;

import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();

    public Libro registrar(String titulo, String autor, String isbn, String editorial, String stockTexto, int idCategoria) {
        if (titulo == null || titulo.trim().isEmpty()) return null;
        if (autor == null || autor.trim().isEmpty()) return null;
        if (idCategoria <= 0) return null;

        Integer stock = parsearEntero(stockTexto);
        if (stock == null || stock < 0) return null;

        Libro libro = new Libro(titulo.trim(), autor.trim(), isbn, editorial, stock, idCategoria);
        boolean ok = libroDAO.create(libro);
        return ok ? libro : null;
    }

    public List<Libro> listar() {
        return libroDAO.readAll();
    }

    public Libro buscarPorId(int id) {
        return libroDAO.buscarPorId(id);
    }

    public boolean editar(int id, String titulo, String autor, String isbn, String editorial, String stockTexto, int idCategoria) {
        if (titulo == null || titulo.trim().isEmpty()) return false;
        if (autor == null || autor.trim().isEmpty()) return false;
        if (idCategoria <= 0) return false;

        Integer stock = parsearEntero(stockTexto);
        if (stock == null || stock < 0) return false;

        Libro libro = new Libro(id, titulo.trim(), autor.trim(), isbn, editorial, stock, idCategoria);
        return libroDAO.update(libro);
    }

    public boolean eliminar(int id) {
        return libroDAO.delete(id);
    }

    private Integer parsearEntero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
