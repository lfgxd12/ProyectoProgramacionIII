package org.example.entregable2.client;

import org.example.entregable2.dto.PartidoDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente socket para operaciones con el calendario.
 */
public class CalendarioServiceClientSocket implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public CalendarioServiceClientSocket(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    private String send(String msg) throws IOException {
        out.write(msg);
        out.write("\n");
        out.flush();
        return in.readLine();
    }

    private void ensureOK(String res) {
        if (res == null) {
            throw new RuntimeException("Sin respuesta del servidor.");
        }
        if (res.startsWith("ERR|")) {
            throw new RuntimeException(res.substring(4));
        }
        if (!res.startsWith("OK|")) {
            throw new RuntimeException("Respuesta inválida: " + res);
        }
    }

    public String generar(boolean idaVuelta) throws IOException {
        String res = send("CALENDARIO_GENERAR|" + idaVuelta);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        return payload;
    }

    public List<PartidoDTO> obtener() throws IOException {
        String res = send("CALENDARIO_OBTENER");
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<PartidoDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) {
            if (!row.isBlank()) {
                list.add(parse(row));
            }
        }
        return list;
    }

    private PartidoDTO parse(String row) {
        // Formato: id;jornada;localCodigo;visitanteCodigo;golesLocal;golesVisitante;estado
        String[] c = row.split(";", -1);

        PartidoDTO p = new PartidoDTO();
        p.setId(Integer.parseInt(c[0]));
        p.setJornada(Integer.parseInt(c[1]));
        p.setEquipoLocalCodigo(c[2]);
        p.setEquipoVisitanteCodigo(c[3]);
        p.setGolesLocal(Integer.parseInt(c[4]));
        p.setGolesVisitante(Integer.parseInt(c[5]));
        p.setEstado(c[6]);
        p.setTieneResultado(!c[6].equals("PENDIENTE"));

        return p;
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}

