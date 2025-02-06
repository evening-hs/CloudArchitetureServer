package server;

public class ClientManager {
    public String ip;
    public int port;
    public String username;

    public ClientManager(String ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }
}
