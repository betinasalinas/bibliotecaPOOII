package controlador;

import dao.EstudianteDAO;
import modelo.Estudiante;

import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public Estudiante registrar(String nombre, String rut, String curso, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        if (rut == null || rut.trim().isEmpty()) return null;

        Estudiante estudiante = new Estudiante(nombre.trim(), rut.trim(), curso, correo);
        boolean ok = estudianteDAO.create(estudiante);
        return ok ? estudiante : null;
    }

    public List<Estudiante> listar() {
        return estudianteDAO.readAll();
    }

    public boolean editar(int id, String nombre, String rut, String curso, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (rut == null || rut.trim().isEmpty()) return false;

        return estudianteDAO.update(new Estudiante(id, nombre.trim(), rut.trim(), curso, correo));
    }

    public boolean eliminar(int id) {
        return estudianteDAO.delete(id);
    }
}
