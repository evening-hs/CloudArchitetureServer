package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket; 

public class AddClient extends Thread {

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;

    public AddClient(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        try {
            dos.writeUTF("Enter username:");
            String username = dis.readUTF();

            dos.writeUTF("Enter password:");
            String password = dis.readUTF();

            if (Server.authenticate(username, password)) {
                dos.writeUTF("Authentication successful");
                System.out.println("Client " + username + " authenticated.");

                ClientManager client = new ClientManager(socket.getInetAddress(), socket.getPort(), username);
                Server.connectedClients.add(client);
            } else {
                dos.writeUTF("Authentication failed");
                System.out.println("Authentication failed for " + username);
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                dis.close();
                dos.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e);
            }
        }
    }
}
