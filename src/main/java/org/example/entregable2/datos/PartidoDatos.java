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

