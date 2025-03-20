package server;

import org.json.JSONObject;

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
            received = dis.readUTF();
            JSONObject receivedJSON = new JSONObject(received);

            if (!receivedJSON.get("command").equals("login")) {
                System.out.println("Client " + socket + " sent "
                        + receivedJSON.get("command"));
                closeConnection();
                return;
            }

            String username = receivedJSON.get("username").toString();
            String password = receivedJSON.get("password").toString();
            boolean ok = Server.authenticate(username, password);

            JSONObject response = new JSONObject();
            response.put("username", "server");

            if (ok) {
                response.put("command", "ok");
            } else {
                response.put("command", "error");
                response.put("message", "Invalid username or password");
            }

            dos.writeUTF(response.toString());
            dos.flush();

            if (!ok) {
                closeConnection();
                return;
            }

            ClientManager client = new ClientManager(
                    socket.getInetAddress(),socket.getPort(),
                    received,socket, dis, dos);
            Server.connectedClients.add(client);
            client.start();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            closeConnection();
        }
    }
}
