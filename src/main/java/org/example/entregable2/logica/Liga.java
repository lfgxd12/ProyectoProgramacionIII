package org.example.entregable2.logica;

import org.example.entregable2.data.ListaEquipos;

import java.util.*;
import java.util.stream.Collectors;

public class Liga {
    private String nombre;
    private ListaEquipos equipos;
    private List<Partido> calendario;
    private Map<Integer, List<Partido>> partidosPorJornada;
    private int contadorPartidos;

    public Liga(String nombre) {
        this.nombre = nombre;
        this.equipos = new ListaEquipos();
        this.calendario = new ArrayList<>();
        this.partidosPorJornada = new HashMap<>();
        this.contadorPartidos = 1;
    }

    public void registrarEquipo(Equipo e) {
        if (buscarEquipoPorCodigo(e.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un equipo con el código: " + e.getCodigo());
        }
        equipos.agregar(e);
    }

    public void editarEquipo(String codigo, String nuevoNombre, String nuevaCiudad) {
        Equipo equipo = buscarEquipoPorCodigo(codigo);
        if (equipo == null) {
            throw new IllegalArgumentException("No se encontró el equipo con código: " + codigo);
        }
        equipo.setNombre(nuevoNombre);
        equipo.setCiudad(nuevaCiudad);
    }

    public boolean eliminarEquipo(String codigo) {
        Equipo equipo = buscarEquipoPorCodigo(codigo);
        if (equipo == null) {
            return false;
        }

        boolean tienePartidosJugados = calendario.stream()
            .anyMatch(p -> (p.getLocal().equals(equipo) || p.getVisitante().equals(equipo))
                && p.tieneResultado());

        if (tienePartidosJugados) {
            throw new IllegalStateException("No se puede eliminar el equipo porque tiene partidos jugados");
        }

        calendario.removeIf(p -> p.getLocal().equals(equipo) || p.getVisitante().equals(equipo));

        return equipos.eliminar(codigo);
    }

    public void generarCalendario(boolean idaVuelta) {
        if (equipos.getTamanio() < 2) {
            throw new IllegalStateException("Se necesitan al menos 2 equipos para generar el calendario");
        }

        calendario.clear();
        partidosPorJornada.clear();
        contadorPartidos = 1;

        List<Equipo> equiposCalendario = new ArrayList<>(equipos.obtenerTodos());
        int numEquipos = equiposCalendario.size();

        boolean esImpar = numEquipos % 2 != 0;
        if (esImpar) {
            equiposCalendario.add(null);
            numEquipos++;
        }

        int numJornadas = numEquipos - 1;
        int partidosPorJornada = numEquipos / 2;

        for (int jornada = 1; jornada <= numJornadas; jornada++) {
            List<Partido> partidosJornada = new ArrayList<>();

            for (int i = 0; i < partidosPorJornada; i++) {
                int local = i;
                int visitante = numEquipos - 1 - i;

                if (local != visitante) {
                    Equipo equipoLocal = equiposCalendario.get(local);
                    Equipo equipoVisitante = equiposCalendario.get(visitante);

                    if (equipoLocal != null && equipoVisitante != null) {
                        Partido partido = new Partido(equipoLocal, equipoVisitante, new Date(), jornada);
                        partido.setId(contadorPartidos++);
                        calendario.add(partido);
                        partidosJornada.add(partido);
                    }
                }
            }

            if (!partidosJornada.isEmpty()) {
                this.partidosPorJornada.put(jornada, partidosJornada);
            }

            Equipo temp = equiposCalendario.get(numEquipos - 1);
            for (int i = numEquipos - 1; i > 1; i--) {
                equiposCalendario.set(i, equiposCalendario.get(i - 1));
            }
            equiposCalendario.set(1, temp);
        }

        if (idaVuelta) {
            int jornadaVuelta = numJornadas + 1;
            List<Partido> partidosIda = new ArrayList<>(calendario);

            for (Partido partidoIda : partidosIda) {
                Partido partidoVuelta = new Partido(
                    partidoIda.getVisitante(),
                    partidoIda.getLocal(),
                    new Date(),
                    jornadaVuelta + (partidoIda.getJornada() - 1)
                );
                partidoVuelta.setId(contadorPartidos++);
                calendario.add(partidoVuelta);

                this.partidosPorJornada.computeIfAbsent(partidoVuelta.getJornada(), k -> new ArrayList<>())
                    .add(partidoVuelta);
            }
        }
    }

    public List<Partido> listarPartidosPorJornada(int jornada) {
        return partidosPorJornada.getOrDefault(jornada, new ArrayList<>());
    }

    public List<Partido> listarPartidosPorEquipo(String codigo) {
        Equipo equipo = buscarEquipoPorCodigo(codigo);
        if (equipo == null) {
            return new ArrayList<>();
        }

        return calendario.stream()
            .filter(p -> p.getLocal().equals(equipo) || p.getVisitante().equals(equipo))
            .collect(Collectors.toList());
    }

    public Partido obtenerPartidoPorId(int id) {
        return calendario.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public boolean registrarResultadoPartido(int idPartido, int golesLocal, int golesVisitante) {
        Partido partido = obtenerPartidoPorId(idPartido);

        if (partido == null) {
            throw new IllegalArgumentException("No se encontró el partido con ID: " + idPartido);
        }

        if (partido.tieneResultado()) {
            throw new IllegalStateException("Este partido ya tiene un resultado registrado");
        }

        // Registra resultado en el partido
        partido.registrarResultado(golesLocal, golesVisitante);

        // Actualiza estadísticas de los equipos
        partido.getLocal().aplicarResultado(golesLocal, golesVisitante);
        partido.getVisitante().aplicarResultado(golesVisitante, golesLocal);

        return true;
    }

    public List<Equipo> calcularTabla() {
        return equipos.obtenerTodos().stream()
            .sorted((e1, e2) -> {
                int comparacionPuntos = Integer.compare(e2.getPts(), e1.getPts());
                if (comparacionPuntos != 0) return comparacionPuntos;

                int comparacionDG = Integer.compare(e2.getDg(), e1.getDg());
                if (comparacionDG != 0) return comparacionDG;

                int comparacionGF = Integer.compare(e2.getGf(), e1.getGf());
                if (comparacionGF != 0) return comparacionGF;

                return e1.getNombre().compareTo(e2.getNombre());
            })
            .collect(Collectors.toList());
    }

    private Equipo buscarEquipoPorCodigo(String codigo) {
        return equipos.buscarPorCodigo(codigo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Equipo> getEquipos() {
        return new ArrayList<>(equipos.obtenerTodos());
    }

    public List<Partido> getCalendario() {
        return new ArrayList<>(calendario);
    }

    public Map<Integer, List<Partido>> getPartidosPorJornada() {
        return new HashMap<>(partidosPorJornada);
    }
}

