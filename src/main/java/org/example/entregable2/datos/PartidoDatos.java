package org.example.entregable2.datos;

import org.example.entregable2.dto.PartidoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PartidoDatos {

    public int insertar(PartidoDTO partido) {

        String sql = "INSERT INTO partido (IdJornada, IdTemporada, jornada, fecha, equipoLocalCodigo, equipoVisitanteCodigo, " +
                "estadio, golesLocal, golesVisitante, estado, activo) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, partido.getIdJornada());
            ps.setInt(2, partido.getIdTemporada());
            ps.setInt(3, partido.getJornada()); // Mantener por compatibilidad
            ps.setDate(4, toSqlDate(partido.getFecha()));
            ps.setString(5, partido.getEquipoLocalCodigo());
            ps.setString(6, partido.getEquipoVisitanteCodigo());
            ps.setString(7, partido.getEstadio());
            ps.setInt(8, partido.getGolesLocal());
            ps.setInt(9, partido.getGolesVisitante());
            ps.setString(10, partido.getEstado());
            ps.setInt(11, 1); // activo

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo insertar el partido: " + e.getMessage());
        }

        return -1;
    }

    public PartidoDTO obtenerPartidoPorId(int id) {

        String sql = "SELECT * FROM partido WHERE IdPartido = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo partido por id: " + e.getMessage());
        }

        return null;
    }

    public boolean modificar(PartidoDTO partido) {

        String sql = "UPDATE partido SET IdJornada = ?, IdTemporada = ?, jornada = ?, fecha = ?, " +
                "equipoLocalCodigo = ?, equipoVisitanteCodigo = ?, estadio = ?, " +
                "golesLocal = ?, golesVisitante = ?, estado = ? " +
                "WHERE IdPartido = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, partido.getIdJornada());
            ps.setInt(2, partido.getIdTemporada());
            ps.setInt(3, partido.getJornada());
            ps.setDate(4, toSqlDate(partido.getFecha()));
            ps.setString(5, partido.getEquipoLocalCodigo());
            ps.setString(6, partido.getEquipoVisitanteCodigo());
            ps.setString(7, partido.getEstadio());
            ps.setInt(8, partido.getGolesLocal());
            ps.setInt(9, partido.getGolesVisitante());
            ps.setString(10, partido.getEstado());
            ps.setInt(11, partido.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo modificar el partido: " + e.getMessage());
        }
    }

    public boolean eliminar(int id) {

        String sql = "UPDATE partido SET activo = 0 WHERE IdPartido = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el partido: " + e.getMessage());
        }
    }

    public List<PartidoDTO> listaPartidosActivos() {

        String sql = "SELECT * FROM partido WHERE activo = 1 ORDER BY jornada, fecha";
        List<PartidoDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando los partidos activos: " + e.getMessage());
        }

        return lista;
    }

    private static java.sql.Date toSqlDate(Date d) {
        if (d == null) return new java.sql.Date(System.currentTimeMillis());
        return new java.sql.Date(d.getTime());
    }

    private static Date fromSqlDate(java.sql.Date d) {
        if (d == null) return null;
        return new Date(d.getTime());
    }


    public List<PartidoDTO> listaPartidosPorJornada(int idTemporada, int jornada) {

        String sql = "SELECT * FROM partido WHERE activo = 1 AND IdTemporada = ? AND jornada = ? " +
                "ORDER BY fecha ASC";

        List<PartidoDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);
            ps.setInt(2, jornada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToDTO(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando partidos por jornada: " + e.getMessage());
        }

        return lista;
    }

    public List<PartidoDTO> listaPartidosPorTemporada(int idTemporada) {

        String sql = "SELECT * FROM partido WHERE activo = 1 AND IdTemporada = ? " +
                "ORDER BY jornada ASC, fecha ASC";

        List<PartidoDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToDTO(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando partidos por temporada: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarResultado(int idPartido, int golesLocal, int golesVisitante, String estado) {

        String sql = "UPDATE partido SET golesLocal = ?, golesVisitante = ?, estado = ? " +
                "WHERE IdPartido = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, golesLocal);
            ps.setInt(2, golesVisitante);
            ps.setString(3, estado); // ej: "JUGADO"
            ps.setInt(4, idPartido);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando resultado: " + e.getMessage());
        }
    }

    public List<PartidoDTO> listaResultadosPorJornada(int idTemporada, int jornada) {

        String sql = "SELECT * FROM partido " +
                "WHERE activo = 1 AND IdTemporada = ? AND jornada = ? AND estado = 'FINALIZADO' " +
                "ORDER BY fecha ASC";

        List<PartidoDTO> lista = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTemporada);
            ps.setInt(2, jornada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToDTO(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando resultados por jornada: " + e.getMessage());
        }

        return lista;
    }

    private PartidoDTO mapRowToDTO(ResultSet rs) throws SQLException {
        PartidoDTO dto = new PartidoDTO();
        dto.setId(rs.getInt("IdPartido"));
        dto.setIdJornada(rs.getInt("IdJornada"));
        dto.setIdTemporada(rs.getInt("IdTemporada"));
        dto.setJornada(rs.getInt("jornada"));
        dto.setEquipoLocalCodigo(rs.getString("equipoLocalCodigo"));
        dto.setEquipoVisitanteCodigo(rs.getString("equipoVisitanteCodigo"));
        dto.setFecha(fromSqlDate(rs.getDate("fecha")));

        try {
            dto.setEstadio(rs.getString("estadio"));
        } catch (SQLException e) {
            dto.setEstadio("");
        }

        dto.setGolesLocal(rs.getInt("golesLocal"));
        dto.setGolesVisitante(rs.getInt("golesVisitante"));

        try {
            dto.setEstado(rs.getString("estado"));
        } catch (SQLException e) {
            dto.setEstado("PENDIENTE");
        }

        return dto;
    }
}

/*
package org.example.entregable2.datos;

import org.example.entregable2.dto.PartidoDTO;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PartidoDatos {

    private final Path rutaArchivo;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public PartidoDatos(String rutaRelativa) {
        this.rutaArchivo = Paths.get(rutaRelativa);
    }

    public List<PartidoDTO> cargar() throws Exception {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }

        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setNamespaceAware(false);

        DocumentBuilder db = dbf.newDocumentBuilder();

        try (InputStream in = Files.newInputStream(rutaArchivo)) {
            doc = db.parse(in);
        }

        doc.getDocumentElement().normalize();

        List<PartidoDTO> partidos = new ArrayList<>();
        NodeList nodos = doc.getElementsByTagName("partido");

        for (int i = 0; i < nodos.getLength(); i++) {
            Node n = nodos.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;

            Element e = (Element) n;

            int id = parseInt(getText(e, "id"), 0);
            int jornada = parseInt(getText(e, "jornada"), 0);
            String fechaStr = getText(e, "fecha");
            Date fecha = parseFecha(fechaStr);
            String equipoLocalCodigo = getText(e, "equipoLocalCodigo");
            String equipoVisitanteCodigo = getText(e, "equipoVisitanteCodigo");
            int golesLocal = parseInt(getText(e, "golesLocal"), -1);
            int golesVisitante = parseInt(getText(e, "golesVisitante"), -1);

            PartidoDTO dto = new PartidoDTO(id, jornada, equipoLocalCodigo, equipoVisitanteCodigo);
            dto.setFecha(fecha);
            dto.setGolesLocal(golesLocal);
            dto.setGolesVisitante(golesVisitante);

            partidos.add(dto);
        }

        return partidos;
    }

    public void guardar(List<PartidoDTO> partidos) throws Exception {
        if (rutaArchivo.getParent() != null) {
            Files.createDirectories(rutaArchivo.getParent());
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("partidos");
        doc.appendChild(root);

        for (PartidoDTO partido : partidos) {
            Element partidoElement = doc.createElement("partido");
            root.appendChild(partidoElement);

            append(doc, partidoElement, "id", String.valueOf(partido.getId()));
            append(doc, partidoElement, "jornada", String.valueOf(partido.getJornada()));
            append(doc, partidoElement, "fecha", formatFecha(partido.getFecha()));
            append(doc, partidoElement, "equipoLocalCodigo", nullToEmpty(partido.getEquipoLocalCodigo()));
            append(doc, partidoElement, "equipoVisitanteCodigo", nullToEmpty(partido.getEquipoVisitanteCodigo()));
            append(doc, partidoElement, "golesLocal", String.valueOf(partido.getGolesLocal()));
            append(doc, partidoElement, "golesVisitante", String.valueOf(partido.getGolesVisitante()));
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());

        try (OutputStream out = Files.newOutputStream(rutaArchivo,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            t.transform(new DOMSource(doc), new StreamResult(out));
        }
    }

    private static void append(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.appendChild(doc.createTextNode(value == null ? "" : value));
        parent.appendChild(el);
    }

    private static String getText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return "";
        Node n = list.item(0);
        return n == null ? "" : n.getTextContent();
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return def;
        }
    }

    private static Date parseFecha(String s) {
        try {
            return DATE_FORMAT.parse(s.trim());
        } catch (Exception ex) {
            return new Date();
        }
    }

    private static String formatFecha(Date fecha) {
        if (fecha == null) return DATE_FORMAT.format(new Date());
        return DATE_FORMAT.format(fecha);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

 */
