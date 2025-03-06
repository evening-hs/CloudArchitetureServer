/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;

import java.util.LinkedList;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue; 

/**
 *
 * @author uriel
 */
public class Server {
    // Messages queue that stores the messages from all clients.
    public static ConcurrentLinkedQueue<JSONObject> queue
            = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ClientManager> connectedClients
            = new ConcurrentLinkedQueue<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Server starting...");

        Thread multicastThread = new Thread(() -> {
            try {
                multicast();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        multicastThread.start();

        try {
            ServerSocket serverSocket = new ServerSocket(2555);
            System.out.println("ServerSocket: " + serverSocket);
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    
                    System.out.println("A new Client is connected: " + socket);

                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Assigning new Thread for this Client.");

                    Thread t = new AddClient(socket, dis, dos);
                    t.start();
                } catch (Exception e) {
                    assert socket != null;
                    socket.close();
                    System.out.println("Server Error: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e);
        }

    }

    public static Boolean authenticate(String username, String password) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void multicast() throws InterruptedException {
        for (;;) {
            Thread.sleep(50);

            JSONObject message = queue.poll();

            if (message == null) {
                continue;
            }

            String sender = message.getString("username");

            for (ClientManager client : connectedClients) {
                if (!client.username.equals(sender)) {
                    client.queue.add(message);
                }
            }
        }
    }
}

