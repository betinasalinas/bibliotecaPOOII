package dao;

import conexion.DatabaseConnection;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Usuario (login del sistema).
 * Usa la conexión única (Singleton) de DatabaseConnection; por eso NO cierra
 * la Connection, solo los PreparedStatement/ResultSet que crea.
 */
public class UsuarioDAO {

    public boolean create(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, contrasena, rol) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) usuario.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> readAll() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, rut, correo, contrasena, rol FROM usuarios ORDER BY id";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, rut=?, correo=?, contrasena=?, rol=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol());
            ps.setInt(6, usuario.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un usuario por correo y contraseña para el login.
     * Devuelve null si las credenciales no coinciden con ningún usuario.
     */
    public Usuario autenticar(String correo, String contrasena) {
        String sql = "SELECT id, nombre, rut, correo, contrasena, rol FROM usuarios WHERE correo = ? AND contrasena = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("rol")
        );
    }
}
