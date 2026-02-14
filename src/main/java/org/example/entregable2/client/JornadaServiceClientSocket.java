package org.example.entregable2.client;

import org.example.entregable2.dto.JornadaDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente Socket para operaciones de Jornada
 */
public class JornadaServiceClientSocket implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public JornadaServiceClientSocket(String host, int port) throws IOException {
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
     * Lista jornadas por temporada
     */
    public List<JornadaDTO> listarPorTemporada(int idTemporada) throws IOException {
        String res = send("JORNADA_LIST_BY_TEMPORADA|" + idTemporada);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<JornadaDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) list.add(parse(row));
        return list;
    }

    /**
     * Crea una nueva jornada
     */
    public int crear(JornadaDTO j) throws IOException {
        String req = "JORNADA_CREATE|" + j.getIdTemporada() + "|" + j.getNumeroJornada();
        String res = send(req);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;
        return Integer.parseInt(payload.replace("ID=", "").trim());
    }

    /**
     * Obtiene una jornada por ID
     */
    public JornadaDTO obtenerPorId(int id) throws IOException {
        String res = send("JORNADA_GET|" + id);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        if (payload.isBlank()) return null;
        return parse(payload);
    }

    /**
     * Actualiza una jornada
     */
    public void actualizar(JornadaDTO j) throws IOException {
        String req = "JORNADA_UPDATE|" + j.getIdJornada() + "|" + j.getIdTemporada() + "|" + j.getNumeroJornada();
        String res = send(req);
        ensureOK(res);
    }

    /**
     * Elimina una jornada
     */
    public void eliminar(int id) throws IOException {
        String res = send("JORNADA_DELETE|" + id);
        ensureOK(res);
    }

    private JornadaDTO parse(String row) {
        // id;idTemporada;numeroJornada
        String[] c = row.split(";", -1);
        JornadaDTO j = new JornadaDTO();
        j.setIdJornada(Integer.parseInt(c[0]));
        j.setIdTemporada(Integer.parseInt(c[1]));
        j.setNumeroJornada(Integer.parseInt(c[2]));
        return j;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}

