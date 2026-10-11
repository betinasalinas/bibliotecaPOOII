package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

/**
 * Controlador de autenticación: valida credenciales antes de delegar al DAO.
 */
public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Intenta autenticar al usuario. Devuelve el Usuario si las credenciales
     * son correctas, o null si no lo son (o si los campos vienen vacíos).
     */
    public Usuario login(String correo, String contrasena) {
        if (correo == null || correo.trim().isEmpty()) return null;
        if (contrasena == null || contrasena.trim().isEmpty()) return null;

        return usuarioDAO.autenticar(correo.trim(), contrasena);
    }
}
