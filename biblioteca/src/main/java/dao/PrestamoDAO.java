package dao;

import conexion.DatabaseConnection;
import modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Prestamo. CRUD completo con PreparedStatement/ResultSet,
 * más consultas para los reportes (libros más prestados, historial por
 * estudiante, listado de préstamos vigentes).
 */
public class PrestamoDAO {

    public boolean create(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, prestamo.getFechaPrestamo());
            ps.setDate(4, prestamo.getFechaDevolucion());
            ps.setBoolean(5, prestamo.isDevuelto());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) prestamo.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear préstamo: " + e.getMessage());
            return false;
        }
    }

    private static final String SELECT_BASE =
            "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " +
                    "e.nombre AS nombre_estudiante, l.titulo AS titulo_libro " +
                    "FROM prestamos p " +
                    "JOIN estudiantes e ON p.id_estudiante = e.id " +
                    "JOIN libros l ON p.id_libro = l.id ";

    public List<Prestamo> readAll() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.id";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
        }
        return lista;
    }

    public boolean update(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET id_estudiante=?, id_libro=?, fecha_prestamo=?, fecha_devolucion=?, devuelto=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, prestamo.getFechaPrestamo());
            ps.setDate(4, prestamo.getFechaDevolucion());
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca un préstamo como devuelto.
     */
    public boolean marcarDevuelto(int idPrestamo) {
        String sql = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al marcar préstamo como devuelto: " + e.getMessage());
            return false;
        }
    }

    /** Reporte: préstamos actualmente vigentes (no devueltos). */
    public List<Prestamo> listarVigentes() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos vigentes: " + e.getMessage());
        }
        return lista;
    }

    /** Reporte: historial de préstamos de un estudiante específico. */
    public List<Prestamo> historialPorEstudiante(int idEstudiante) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial del estudiante: " + e.getMessage());
        }
        return lista;
    }

    /** Reporte: ranking de libros más prestados (título + cantidad de préstamos). */
    public List<Object[]> librosMasPrestados() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT l.titulo, COUNT(*) AS cantidad " +
                "FROM prestamos p JOIN libros l ON p.id_libro = l.id " +
                "GROUP BY l.id, l.titulo ORDER BY cantidad DESC";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{rs.getString("titulo"), rs.getInt("cantidad")});
            }
        } catch (SQLException e) {
            System.err.println("Error al generar reporte de libros más prestados: " + e.getMessage());
        }
        return lista;
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
                rs.getInt("id"), rs.getInt("id_estudiante"), rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo"), rs.getDate("fecha_devolucion"), rs.getBoolean("devuelto")
        );
        prestamo.setNombreEstudiante(rs.getString("nombre_estudiante"));
        prestamo.setTituloLibro(rs.getString("titulo_libro"));
        return prestamo;
    }
}
