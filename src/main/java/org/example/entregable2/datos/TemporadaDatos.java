package org.example.entregable2.datos;

import org.example.entregable2.dto.TemporadaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TemporadaDatos {

    public int insertar(TemporadaDTO temporada) {
        String sql = "INSERT INTO temporada (nombre, anioInicio, anioFin, fechaInicio, fechaFin, activa) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, temporada.getNombre());
            ps.setInt(2, temporada.getAnioInicio());
            ps.setInt(3, temporada.getAnioFin());
            ps.setDate(4, toSqlDate(temporada.getFechaInicio()));
            ps.setDate(5, toSqlDate(temporada.getFechaFin()));
            ps.setBoolean(6, temporada.isActiva());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo insertar la temporada: " + e.getMessage(), e);
        }

        return -1;
    }

    public TemporadaDTO obtenerPorId(int idTemporada) {
        String sql = "SELECT * FROM temporada WHERE IdTemporada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo temporada por ID: " + e.getMessage(), e);
        }

        return null;
    }

    public TemporadaDTO obtenerTemporadaActiva() {
        String sql = "SELECT * FROM temporada WHERE activa = 1 LIMIT 1";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRowToDTO(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo temporada activa: " + e.getMessage(), e);
        }

        return null;
    }

    public boolean modificar(TemporadaDTO temporada) {
        String sql = "UPDATE temporada SET nombre = ?, anioInicio = ?, anioFin = ?, " +
                     "fechaInicio = ?, fechaFin = ?, activa = ? WHERE IdTemporada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, temporada.getNombre());
            ps.setInt(2, temporada.getAnioInicio());
            ps.setInt(3, temporada.getAnioFin());
            ps.setDate(4, toSqlDate(temporada.getFechaInicio()));
            ps.setDate(5, toSqlDate(temporada.getFechaFin()));
            ps.setBoolean(6, temporada.isActiva());
            ps.setInt(7, temporada.getIdTemporada());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo modificar la temporada: " + e.getMessage(), e);
        }
    }

    public boolean desactivar(int idTemporada) {
        String sql = "UPDATE temporada SET activa = 0 WHERE IdTemporada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo desactivar la temporada: " + e.getMessage(), e);
        }
    }

    public boolean activarTemporada(int idTemporada) {
        try (Connection con = ConectionFactory.getConnection()) {
            con.setAutoCommit(false);

            try {
                String sqlDesactivar = "UPDATE temporada SET activa = 0";
                try (PreparedStatement ps = con.prepareStatement(sqlDesactivar)) {
                    ps.executeUpdate();
                }

                String sqlActivar = "UPDATE temporada SET activa = 1 WHERE IdTemporada = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlActivar)) {
                    ps.setInt(1, idTemporada);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo activar la temporada: " + e.getMessage(), e);
        }
    }

    public List<TemporadaDTO> listarTodas() {
        String sql = "SELECT * FROM temporada ORDER BY anioInicio DESC";
        List<TemporadaDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando temporadas: " + e.getMessage(), e);
        }

        return lista;
    }

    public boolean eliminar(int idTemporada) {
        String sql = "DELETE FROM temporada WHERE IdTemporada = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar la temporada: " + e.getMessage(), e);
        }
    }

    private TemporadaDTO mapRowToDTO(ResultSet rs) throws SQLException {
        TemporadaDTO dto = new TemporadaDTO();
        dto.setIdTemporada(rs.getInt("IdTemporada"));
        dto.setNombre(rs.getString("nombre"));
        dto.setAnioInicio(rs.getInt("anioInicio"));
        dto.setAnioFin(rs.getInt("anioFin"));
        dto.setFechaInicio(fromSqlDate(rs.getDate("fechaInicio")));
        dto.setFechaFin(fromSqlDate(rs.getDate("fechaFin")));
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

