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

            // Estadísticas
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

