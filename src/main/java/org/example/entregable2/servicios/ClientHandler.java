package org.example.entregable2.servicios;

import org.example.entregable2.datos.EquipoDatos;
import org.example.entregable2.dto.EquipoDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final EquipoDatos dao = new EquipoDatos();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            String req;
            while ((req = in.readLine()) != null) {
                String res = procesar(req);
                out.write(res);
                out.write("\n");
                out.flush();
            }

        } catch (Exception e) {
            System.err.println("Cliente desconectado/error: " + e.getMessage());
        }
    }

    private String procesar(String req) {
        try {
            String[] p = req.split("\\|", -1);
            String cmd = p[0].trim().toUpperCase();

            switch (cmd) {
                case "LIST": {
                    List<EquipoDTO> equipos = dao.listarActivos();
                    return "OK|" + serializarLista(equipos);
                }
                case "CREATE": {
                    EquipoDTO e = new EquipoDTO();
                    e.setNombre(p[1]);
                    e.setCiudad(p[2]);
                    e.setEstadio(p[3]);
                    e.setAnioFundacion(Integer.parseInt(p[4]));
                    int id = dao.insertar(e);
                    return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar";
                }
                case "UPDATE": {
                    EquipoDTO e = new EquipoDTO();
                    e.setIdEquipo(Integer.parseInt(p[1]));
                    e.setNombre(p[2]);
                    e.setCiudad(p[3]);
                    e.setEstadio(p[4]);
                    e.setAnioFundacion(Integer.parseInt(p[5]));
                    boolean ok = dao.modificar(e);
                    return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar";
                }
                case "DELETE": {
                    int id = Integer.parseInt(p[1]);
                    boolean ok = dao.eliminar(id);
                    return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar";
                }
                default:
                    return "ERR|Comando no soportado: " + cmd;
            }

        } catch (Exception ex) {
            return "ERR|" + ex.getMessage();
        }
    }

    private String serializarLista(List<EquipoDTO> equipos) {
        // Cada equipo: id;nombre;ciudad;estadio;anio  y separados por ||
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipos.size(); i++) {
            EquipoDTO e = equipos.get(i);
            sb.append(e.getIdEquipo()).append(";")
                    .append(clean(e.getNombre())).append(";")
                    .append(clean(e.getCiudad())).append(";")
                    .append(clean(e.getEstadio())).append(";")
                    .append(e.getAnioFundacion());
            if (i < equipos.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("|", " ").replace(";", " ").replace("||", " ");
    }
}
