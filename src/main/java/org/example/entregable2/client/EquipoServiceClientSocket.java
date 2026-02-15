package org.example.entregable2.client;

import org.example.entregable2.dto.EquipoDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class EquipoServiceClientSocket implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public EquipoServiceClientSocket(String host, int port) throws IOException {
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

    public List<EquipoDTO> listar() throws IOException {
        String res = send("LIST");
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        List<EquipoDTO> list = new ArrayList<>();
        if (payload.isBlank()) return list;

        String[] rows = payload.split("\\|\\|");
        for (String row : rows) {
            if (!row.isBlank()) {
                list.add(parse(row));
            }
        }
        return list;
    }

    public EquipoDTO obtener(String codigo) throws IOException {
        String res = send("GET|" + codigo);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        if (payload.isBlank()) {
            throw new RuntimeException("Equipo no encontrado");
        }

        return parse(payload);
    }

    public int crear(EquipoDTO e) throws IOException {
        String req = "CREATE|" +
                     clean(e.getCodigo()) + "|" +
                     clean(e.getNombre()) + "|" +
                     clean(e.getCiudad()) + "|" +
                     clean(e.getEstadio()) + "|" +
                     e.getAnioFundacion();

        String res = send(req);
        ensureOK(res);

        String payload = res.substring(3);
        payload = payload.startsWith("|") ? payload.substring(1) : payload;

        return Integer.parseInt(payload.replace("ID=", "").trim());
    }

    public void actualizar(EquipoDTO e) throws IOException {
        String req = "UPDATE|" +
                     e.getIdEquipo() + "|" +
                     clean(e.getCodigo()) + "|" +
                     clean(e.getNombre()) + "|" +
                     clean(e.getCiudad()) + "|" +
                     clean(e.getEstadio()) + "|" +
                     e.getAnioFundacion();

        String res = send(req);
        ensureOK(res);
    }

    public void eliminar(String codigo) throws IOException {
        String res = send("DELETE|" + clean(codigo));
        ensureOK(res);
    }

    private EquipoDTO parse(String row) {
        String[] c = row.split(";", -1);

        EquipoDTO e = new EquipoDTO();
        e.setCodigo(c[0]);
        e.setNombre(c[1]);
        e.setCiudad(c[2]);
        e.setEstadio(c[3]);
        e.setAnioFundacion(Integer.parseInt(c[4]));

        if (c.length > 5) {
            e.setPj(Integer.parseInt(c[5]));
            e.setG(Integer.parseInt(c[6]));
            e.setE(Integer.parseInt(c[7]));
            e.setP(Integer.parseInt(c[8]));
            e.setGf(Integer.parseInt(c[9]));
            e.setGc(Integer.parseInt(c[10]));
            e.setDg(Integer.parseInt(c[11]));
            e.setPts(Integer.parseInt(c[12]));
        }

        return e;
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("|", " ").replace(";", " ").trim();
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}

