package server;

import java.net.InetAddress;

public class ClientManager {
    public InetAddress ip;
    public int port;
    public String username;

    public ClientManager(InetAddress ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }
}
