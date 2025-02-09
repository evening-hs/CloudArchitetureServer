package server;

//import org.json.JSONObject;
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
        while (true) {
            try {
                dos.writeUTF("Add username");
                received = dis.readUTF();
                //json
                if (received.equals("Exit")) {
                    System.out.println("Client " + socket + " sends Exit...");
                    System.out.println("Closing the connection.");
                    socket.close();
                    System.out.println("Connection closed.");
                    break;
                }
                System.out.println(received);

                ClientManager client = new ClientManager(socket.getInetAddress(), socket.getPort() , received);
                Server.connectedClients.add(client);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        try {
            dis.close();
            dos.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }

}
