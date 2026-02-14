package org.example.entregable2.datos;

import org.example.entregable2.dto.EquipoDTO;
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
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDatos {

    private static final String SQL_UPDATE =
            "UPDATE equipos SET nombre=?, ciudad=?, estadio=?, anio_fundacion=?, entrenador=?, activo=? " +
                    "WHERE id_equipo=?";

    public EquipoDatos() {}

    public int insertar(EquipoDTO equipo) {

        String sql = "INSERT INTO equipo " +
                "(codigo, nombre, ciudad, estadio, anio_fundacion, pj, g, e, p, gf, gc, dg, pts, activo) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipo.getCodigo());
            ps.setString(2, equipo.getNombre());
            ps.setString(3, equipo.getCiudad());
            ps.setString(4, equipo.getEstadio());
            ps.setInt(5, equipo.getAnioFundacion());

            ps.setInt(6, equipo.getPj());
            ps.setInt(7, equipo.getG());
            ps.setInt(8, equipo.getE());
            ps.setInt(9, equipo.getP());
            ps.setInt(10, equipo.getGf());
            ps.setInt(11, equipo.getGc());
            ps.setInt(12, equipo.getDg());
            ps.setInt(13, equipo.getPts());

            ps.setInt(14, equipo.isEliminado() ? 0 : 1);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo insertar el equipo: " + e.getMessage(), e);
        }

        return -1;
    }

    public EquipoDTO obtenerEquipoPorId(int id) {

        String sql = "SELECT * FROM equipo WHERE IdEquipo = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo el equipo por id: " + e.getMessage(), e);
        }

        return null;
    }

    public boolean modificar(EquipoDTO equipo) {

        String sql = "UPDATE equipo SET " +
                "codigo=?, nombre=?, ciudad=?, estadio=?, anio_fundacion=?, " +
                "pj=?, g=?, e=?, p=?, gf=?, gc=?, dg=?, pts=?, activo=? " +
                "WHERE IdEquipo=?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getCodigo());
            ps.setString(2, equipo.getNombre());
            ps.setString(3, equipo.getCiudad());
            ps.setString(4, equipo.getEstadio());
            ps.setInt(5, equipo.getAnioFundacion());

            ps.setInt(6, equipo.getPj());
            ps.setInt(7, equipo.getG());
            ps.setInt(8, equipo.getE());
            ps.setInt(9, equipo.getP());
            ps.setInt(10, equipo.getGf());
            ps.setInt(11, equipo.getGc());
            ps.setInt(12, equipo.getDg());
            ps.setInt(13, equipo.getPts());

            ps.setInt(14, equipo.isEliminado() ? 0 : 1);
            ps.setInt(15, equipo.getIdEquipo());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo modificar el equipo: " + e.getMessage(), e);
        }
    }

    public boolean  eliminar(int idEquipo) {

        String sql = "UPDATE equipo SET activo = 0 WHERE IdEquipo = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar (inactivar) el equipo: " + e.getMessage(), e);
        }
    }

    public List<EquipoDTO> listaEquipoActivos() {

        String sql = "SELECT * FROM equipo WHERE activo = 1";
        List<EquipoDTO> listaEquipo = new ArrayList<>();

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listaEquipo.add(mapRowToDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando los equipos activos: " + e.getMessage(), e);
        }

        return listaEquipo;
    }

    private EquipoDTO mapRowToDTO(ResultSet rs) throws SQLException {
        EquipoDTO dto = new EquipoDTO();
        dto.setIdEquipo(rs.getInt("IdEquipo"));
        dto.setCodigo(rs.getString("codigo"));
        dto.setNombre(rs.getString("nombre"));
        dto.setCiudad(rs.getString("ciudad"));
        dto.setEstadio(rs.getString("estadio"));
        dto.setAnioFundacion(rs.getInt("anio_fundacion"));

        dto.setPj(rs.getInt("pj"));
        dto.setG(rs.getInt("g"));
        dto.setE(rs.getInt("e"));
        dto.setP(rs.getInt("p"));
        dto.setGf(rs.getInt("gf"));
        dto.setGc(rs.getInt("gc"));
        dto.setDg(rs.getInt("dg"));
        dto.setPts(rs.getInt("pts"));

        dto.setEliminado(rs.getInt("activo") == 0);
        return dto;
    }

    public EquipoDTO obtenerEquipoPorCodigo(String codigo) {

        String sql = "SELECT * FROM equipo WHERE codigo = ?";

        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EquipoDTO dto = new EquipoDTO();
                    dto.setIdEquipo(rs.getInt("IdEquipo"));
                    dto.setCodigo(rs.getString("codigo"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setEstadio(rs.getString("estadio"));
                    dto.setAnioFundacion(rs.getInt("anio_fundacion"));
                    dto.setPj(rs.getInt("pj"));
                    dto.setG(rs.getInt("g"));
                    dto.setE(rs.getInt("e"));
                    dto.setP(rs.getInt("p"));
                    dto.setGf(rs.getInt("gf"));
                    dto.setGc(rs.getInt("gc"));
                    dto.setDg(rs.getInt("dg"));
                    dto.setPts(rs.getInt("pts"));
                    dto.setEliminado(rs.getInt("activo") == 0);
                    return dto;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo equipo por codigo: " + e.getMessage());
        }

        return null;
    }

    public boolean actualizar(EquipoDTO equipo) {
        try (Connection con = ConectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, equipo.getNombre());
            ps.setString(2, equipo.getCiudad());
            ps.setString(3, equipo.getEstadio());
            ps.setInt(4, equipo.getAnioFundacion());
            ps.setString(5, null);   // entrenador por ahora
            ps.setBoolean(6, true);  // activo por ahora
            ps.setInt(7, equipo.getIdEquipo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando equipo: " + e.getMessage(), e);
        }
    }

}

/*
public class EquipoDatos {

    private final Path rutaArchivo;

    public EquipoDatos(String rutaRelativa) {
        this.rutaArchivo = Paths.get(rutaRelativa);
    }

    public List<EquipoDTO> cargar() throws Exception {
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

        List<EquipoDTO> equipos = new ArrayList<>();
        NodeList nodos = doc.getElementsByTagName("equipo");

        for (int i = 0; i < nodos.getLength(); i++) {
            Node n = nodos.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;

            Element e = (Element) n;

            int idEquipo = parseInt(getText(e, "idEquipo"), 0);
            String codigo = getText(e, "codigo");
            String nombre = getText(e, "nombre");
            String ciudad = getText(e, "ciudad");
            String estadio = getText(e, "estadio");
            int anioFundacion = parseInt(getText(e, "anioFundacion"), 0);
            boolean eliminado = parseBoolean(getText(e, "eliminado"), false);

            int pj = parseInt(getText(e, "pj"), 0);
            int g = parseInt(getText(e, "g"), 0);
            int empates = parseInt(getText(e, "e"), 0);
            int perdidos = parseInt(getText(e, "p"), 0);
            int gf = parseInt(getText(e, "gf"), 0);
            int gc = parseInt(getText(e, "gc"), 0);
            int dg = parseInt(getText(e, "dg"), 0);
            int pts = parseInt(getText(e, "pts"), 0);

            EquipoDTO dto = new EquipoDTO(idEquipo, codigo, nombre, ciudad, estadio, anioFundacion);
            dto.setEliminado(eliminado);
            dto.setPj(pj);
            dto.setG(g);
            dto.setE(empates);
            dto.setP(perdidos);
            dto.setGf(gf);
            dto.setGc(gc);
            dto.setDg(dg);
            dto.setPts(pts);

            equipos.add(dto);
        }

        return equipos;
    }

    public void guardar(List<EquipoDTO> equipos) throws Exception {
        if (rutaArchivo.getParent() != null) {
            Files.createDirectories(rutaArchivo.getParent());
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("equipos");
        doc.appendChild(root);

        for (EquipoDTO eq : equipos) {
            Element equipo = doc.createElement("equipo");
            root.appendChild(equipo);

            append(doc, equipo, "idEquipo", String.valueOf(eq.getIdEquipo()));
            append(doc, equipo, "codigo", nullToEmpty(eq.getCodigo()));
            append(doc, equipo, "nombre", nullToEmpty(eq.getNombre()));
            append(doc, equipo, "ciudad", nullToEmpty(eq.getCiudad()));
            append(doc, equipo, "estadio", nullToEmpty(eq.getEstadio()));
            append(doc, equipo, "anioFundacion", String.valueOf(eq.getAnioFundacion()));
            append(doc, equipo, "eliminado", String.valueOf(eq.isEliminado()));

            append(doc, equipo, "pj", String.valueOf(eq.getPj()));
            append(doc, equipo, "g", String.valueOf(eq.getG()));
            append(doc, equipo, "e", String.valueOf(eq.getE()));
            append(doc, equipo, "p", String.valueOf(eq.getP()));
            append(doc, equipo, "gf", String.valueOf(eq.getGf()));
            append(doc, equipo, "gc", String.valueOf(eq.getGc()));
            append(doc, equipo, "dg", String.valueOf(eq.getDg()));
            append(doc, equipo, "pts", String.valueOf(eq.getPts()));
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

    private static boolean parseBoolean(String s, boolean def) {
        try {
            return Boolean.parseBoolean(s.trim());
        } catch (Exception ex) {
            return def;
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

 */