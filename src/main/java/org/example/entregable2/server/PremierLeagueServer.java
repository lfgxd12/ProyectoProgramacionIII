package org.example.entregable2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Servidor TCP para gestión de la Premier League
public class PremierLeagueServer {

    private static final int PORT = 5050;
    private static final ExecutorService POOL = Executors.newFixedThreadPool(30);

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("Servidor Premier League");
        System.out.println("=====================================");
        System.out.println("Puerto: " + PORT);
        System.out.println("Hilos disponibles: 30");
        System.out.println("Estado: INICIANDO...");
        System.out.println("=====================================\n");

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Servidor listo en puerto " + PORT);
            System.out.println("Esperando conexiones...\n");

            while (true) {
                Socket client = server.accept();
                System.out.println("Cliente conectado: " + client.getInetAddress().getHostAddress() +
                                 ":" + client.getPort());
                POOL.submit(new ClientHandler(client));
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nCerrando servidor...");
            POOL.shutdown();
        }
    }
}

