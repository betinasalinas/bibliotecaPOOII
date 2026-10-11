package dao;

import conexion.DatabaseConnection;
import modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Estudiante. CRUD completo con PreparedStatement/ResultSet.
 */
public class EstudianteDAO {

    public boolean create(Estudiante estudiante) {
        String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) estudiante.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear estudiante: " + e.getMessage());
            return false;
        }
    }

    public List<Estudiante> readAll() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, rut, curso, correo FROM estudiantes ORDER BY id";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Estudiante(
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("rut"),
                        rs.getString("curso"), rs.getString("correo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    public boolean update(Estudiante estudiante) {
        String sql = "UPDATE estudiantes SET nombre=?, rut=?, curso=?, correo=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM estudiantes WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }
}
