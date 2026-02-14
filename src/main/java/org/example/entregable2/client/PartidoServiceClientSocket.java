package org.example.entregable2.client;

import org.example.entregable2.dto.PartidoDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cliente Socket para operaciones de Partido
 */
public class PartidoServiceClientSocket implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PartidoServiceClientSocket(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    private String send(String msg) throws IOException {
        out.write(msg);
        out.write("\n");
        out.flush();
        return in.readLine();
    }

    private void ensureOK(String res) {
        if (res == null) throw new RuntimeException("Sin respuesta del servidor.");
        if (res.startsWith("ERR|")) throw new RuntimeException(res.substring(4));
        if (!res.startsWith("OK|")) throw new RuntimeException("Respuesta inválida: " + res);
    }

    /**
     * Lista partidos por jornada
     */
    public List<PartidoDTO> listarPorJornada(int idJornada) throws IOException {
        String res = send("PARTIDO_LIST_BY_JORNADA|" + idJornada);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<PartidoDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) list.add(parse(row));
        return list;
    }

    /**
     * Lista todos los partidos de una temporada
     */
    public List<PartidoDTO> listarPorTemporada(int idTemporada) throws IOException {
        String res = send("PARTIDO_LIST_BY_TEMPORADA|" + idTemporada);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<PartidoDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) list.add(parse(row));
        return list;
    }

    /**
     * Crea un nuevo partido
     */
    public int crear(PartidoDTO p) throws IOException {
        String req = "PARTIDO_CREATE|" + p.getIdJornada() + "|" + p.getIdTemporada() + "|" +
                     p.getEquipoLocalCodigo() + "|" + p.getEquipoVisitanteCodigo() + "|" +
                     p.getEstadio() + "|" + formatDate(p.getFecha());
        String res = send(req);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;
        return Integer.parseInt(payload.replace("ID=", "").trim());
    }

    /**
     * Registra el resultado de un partido
     */
    public void registrarResultado(int idPartido, int golesLocal, int golesVisitante) throws IOException {
        String req = "PARTIDO_RESULT|" + idPartido + "|" + golesLocal + "|" + golesVisitante;
        String res = send(req);
        ensureOK(res);
    }

    /**
     * Actualiza un partido
     */
    public void actualizar(PartidoDTO p) throws IOException {
        String req = "PARTIDO_UPDATE|" + p.getId() + "|" + p.getIdJornada() + "|" + p.getIdTemporada() + "|" +
                     p.getEquipoLocalCodigo() + "|" + p.getEquipoVisitanteCodigo() + "|" +
                     p.getEstadio() + "|" + formatDate(p.getFecha()) + "|" +
                     p.getGolesLocal() + "|" + p.getGolesVisitante() + "|" + p.getEstado();
        String res = send(req);
        ensureOK(res);
    }

    /**
     * Elimina un partido
     */
    public void eliminar(int id) throws IOException {
        String res = send("PARTIDO_DELETE|" + id);
        ensureOK(res);
    }

    private PartidoDTO parse(String row) {
        // id;idJornada;idTemporada;equipoLocalCodigo;equipoVisitanteCodigo;estadio;fecha;golesLocal;golesVisitante;estado
        String[] c = row.split(";", -1);
        PartidoDTO p = new PartidoDTO();
        p.setId(Integer.parseInt(c[0]));
        p.setIdJornada(Integer.parseInt(c[1]));
        p.setIdTemporada(Integer.parseInt(c[2]));
        p.setEquipoLocalCodigo(c[3]);
        p.setEquipoVisitanteCodigo(c[4]);
        p.setEstadio(c[5]);
        p.setFecha(parseDate(c[6]));
        p.setGolesLocal(c[7].isEmpty() ? -1 : Integer.parseInt(c[7]));
        p.setGolesVisitante(c[8].isEmpty() ? -1 : Integer.parseInt(c[8]));
        p.setEstado(c[9]);
        return p;
    }

    private String formatDate(Date date) {
        return date == null ? "" : sdf.format(date);
    }

    private Date parseDate(String dateStr) {
        try {
            return (dateStr == null || dateStr.isBlank()) ? null : sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}

