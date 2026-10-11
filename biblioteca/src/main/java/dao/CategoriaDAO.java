package dao;

import conexion.DatabaseConnection;
import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Categoria. CRUD completo con PreparedStatement/ResultSet.
 */
public class CategoriaDAO {

    public boolean create(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categoria.getNombre());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) categoria.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> readAll() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY id";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }

    public boolean update(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
