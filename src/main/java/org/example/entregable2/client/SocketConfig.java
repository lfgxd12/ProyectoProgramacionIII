package org.example.entregable2.client;

import java.io.InputStream;
import java.util.Properties;

public class SocketConfig {

    private static String host;
    private static int port;

    static {
        try (InputStream is = SocketConfig.class.getResourceAsStream("/socket.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                host = props.getProperty("socket.host", "127.0.0.1");
                port = Integer.parseInt(props.getProperty("socket.port", "5050"));
            } else {
                host = "127.0.0.1";
                port = 5050;
            }
        } catch (Exception e) {
            host = "127.0.0.1";
            port = 5050;
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static void setHost(String newHost) {
        host = newHost;
    }

    public static void setPort(int newPort) {
        port = newPort;
    }
}


