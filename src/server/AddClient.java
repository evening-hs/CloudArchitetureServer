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
        String received;
        try {
            JSONObject sendJSON = new JSONObject();
            sendJSON.put("command", "username");
            dos.writeUTF(sendJSON.toString());
            received = dis.readUTF();

            JSONObject receivedJSON = new JSONObject(received);
            if (receivedJSON.has("command") && receivedJSON.get("command").equals("exit")) {
                System.out.println("Client " + socket + " sends Exit...");
                System.out.println("Closing the connection.");
                socket.close();
                System.out.println("Connection closed.");
            }
            System.out.println(received);

            ClientManager client = new ClientManager(
                    socket.getInetAddress(),socket.getPort(),
                    received,socket, dis, dos);
            Server.connectedClients.add(client);
            client.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
