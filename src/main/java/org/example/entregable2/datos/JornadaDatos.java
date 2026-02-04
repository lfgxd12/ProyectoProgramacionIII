package org.example.entregable2.datos;

import org.example.entregable2.dto.JornadaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JornadaDatos {

    public int insertar(JornadaDTO jornada) {
        String sql = "INSERT INTO jornada (IdTemporada, numeroJornada, nombre, fechaProgramada, estado, activa) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, jornada.getIdTemporada());
            ps.setInt(2, jornada.getNumeroJornada());
            ps.setString(3, jornada.getNombre());
            ps.setDate(4, toSqlDate(jornada.getFechaProgramada()));
            ps.setString(5, jornada.getEstado());
            ps.setBoolean(6, jornada.isActiva());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo insertar la jornada: " + e.getMessage(), e);
        }

        return -1;
    }

    public JornadaDTO obtenerPorId(int idJornada) {
        String sql = "SELECT * FROM jornada WHERE IdJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idJornada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo jornada por ID: " + e.getMessage(), e);
        }

        return null;
    }

    public JornadaDTO obtenerPorTemporadaYNumero(int idTemporada, int numeroJornada) {
        String sql = "SELECT * FROM jornada WHERE IdTemporada = ? AND numeroJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);
            ps.setInt(2, numeroJornada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo jornada: " + e.getMessage(), e);
        }

        return null;
    }

    public List<JornadaDTO> listarPorTemporada(int idTemporada) {
        String sql = "SELECT * FROM jornada WHERE IdTemporada = ? AND activa = 1 ORDER BY numeroJornada";
        List<JornadaDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToDTO(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando jornadas: " + e.getMessage(), e);
        }

        return lista;
    }

    public boolean modificar(JornadaDTO jornada) {
        String sql = "UPDATE jornada SET numeroJornada = ?, nombre = ?, fechaProgramada = ?, " +
                     "estado = ?, activa = ? WHERE IdJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jornada.getNumeroJornada());
            ps.setString(2, jornada.getNombre());
            ps.setDate(3, toSqlDate(jornada.getFechaProgramada()));
            ps.setString(4, jornada.getEstado());
            ps.setBoolean(5, jornada.isActiva());
            ps.setInt(6, jornada.getIdJornada());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo modificar la jornada: " + e.getMessage(), e);
        }
    }

    public boolean actualizarEstado(int idJornada, String estado) {
        String sql = "UPDATE jornada SET estado = ? WHERE IdJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idJornada);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el estado de la jornada: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(int idJornada) {
        String sql = "UPDATE jornada SET activa = 0 WHERE IdJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idJornada);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar la jornada: " + e.getMessage(), e);
        }
    }

    public boolean eliminarFisico(int idJornada) {
        String sql = "DELETE FROM jornada WHERE IdJornada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idJornada);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar físicamente la jornada: " + e.getMessage(), e);
        }
    }

    public int contarJornadasPorTemporada(int idTemporada) {
        String sql = "SELECT COUNT(*) FROM jornada WHERE IdTemporada = ? AND activa = 1";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error contando jornadas: " + e.getMessage(), e);
        }

        return 0;
    }

    public int obtenerMaximoNumeroJornada(int idTemporada) {
        String sql = "SELECT MAX(numeroJornada) FROM jornada WHERE IdTemporada = ? AND activa = 1";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo máximo número de jornada: " + e.getMessage(), e);
        }

        return 0;
    }

    private JornadaDTO mapRowToDTO(ResultSet rs) throws SQLException {
        JornadaDTO dto = new JornadaDTO();
        dto.setIdJornada(rs.getInt("IdJornada"));
        dto.setIdTemporada(rs.getInt("IdTemporada"));
        dto.setNumeroJornada(rs.getInt("numeroJornada"));
        dto.setNombre(rs.getString("nombre"));
        dto.setFechaProgramada(fromSqlDate(rs.getDate("fechaProgramada")));
        dto.setEstado(rs.getString("estado"));
        dto.setActiva(rs.getBoolean("activa"));
        return dto;
    }

    private java.sql.Date toSqlDate(Date fecha) {
        if (fecha == null) return null;
        return new java.sql.Date(fecha.getTime());
    }

    private Date fromSqlDate(java.sql.Date fecha) {
        if (fecha == null) return null;
        return new Date(fecha.getTime());
    }
}

