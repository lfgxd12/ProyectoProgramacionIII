package org.example.entregable2.client;

import org.example.entregable2.dto.TemporadaDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cliente Socket para operaciones de Temporada
 */
public class TemporadaServiceClientSocket implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public TemporadaServiceClientSocket(String host, int port) throws IOException {
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
     * Lista todas las temporadas activas
     */
    public List<TemporadaDTO> listar() throws IOException {
        String res = send("TEMPORADA_LIST");
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<TemporadaDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) list.add(parse(row));
        return list;
    }

    /**
     * Obtiene la temporada activa
     */
    public TemporadaDTO obtenerActiva() throws IOException {
        String res = send("TEMPORADA_GET_ACTIVE");
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        if (payload.isBlank()) return null;
        return parse(payload);
    }

    /**
     * Crea una nueva temporada
     */
    public int crear(TemporadaDTO t) throws IOException {
        String req = "TEMPORADA_CREATE|" + t.getNombre() + "|" + t.getAnioInicio() + "|" + t.getAnioFin() + "|" +
                     formatDate(t.getFechaInicio()) + "|" + formatDate(t.getFechaFin()) + "|" + (t.isActiva() ? "1" : "0");
        String res = send(req);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;
        return Integer.parseInt(payload.replace("ID=", "").trim());
    }

    /**
     * Actualiza una temporada existente
     */
    public void actualizar(TemporadaDTO t) throws IOException {
        String req = "TEMPORADA_UPDATE|" + t.getIdTemporada() + "|" + t.getNombre() + "|" +
                     t.getAnioInicio() + "|" + t.getAnioFin() + "|" +
                     formatDate(t.getFechaInicio()) + "|" + formatDate(t.getFechaFin()) + "|" + (t.isActiva() ? "1" : "0");
        String res = send(req);
        ensureOK(res);
    }

    /**
     * Elimina (desactiva) una temporada
     */
    public void eliminar(int id) throws IOException {
        String res = send("TEMPORADA_DELETE|" + id);
        ensureOK(res);
    }

    private TemporadaDTO parse(String row) {
        // id;nombre;anioInicio;anioFin;fechaInicio;fechaFin;activa
        String[] c = row.split(";", -1);
        TemporadaDTO t = new TemporadaDTO();
        t.setIdTemporada(Integer.parseInt(c[0]));
        t.setNombre(c[1]);
        t.setAnioInicio(Integer.parseInt(c[2]));
        t.setAnioFin(Integer.parseInt(c[3]));
        t.setFechaInicio(parseDate(c[4]));
        t.setFechaFin(parseDate(c[5]));
        t.setActiva("1".equals(c[6]));
        return t;
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

