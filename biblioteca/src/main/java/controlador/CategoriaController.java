package controlador;

import dao.CategoriaDAO;
import modelo.Categoria;

import java.util.List;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public Categoria registrar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        Categoria categoria = new Categoria(nombre.trim());
        boolean ok = categoriaDAO.create(categoria);
        return ok ? categoria : null;
    }

    public List<Categoria> listar() {
        return categoriaDAO.readAll();
    }

    public boolean editar(int id, String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        return categoriaDAO.update(new Categoria(id, nombre.trim()));
    }

    public boolean eliminar(int id) {
        return categoriaDAO.delete(id);
    }
}
