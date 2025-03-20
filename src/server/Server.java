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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public static final String URL = "jdbc:mysql://localhost:3306/restordb"; //cambiar a URL de la base que usemos
    public static final String USER = "root"; 
    public static final String PSWD = "Win2002Racedb$"; //cambiar a contraseña de la base

    public static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded!");
        } catch (ClassNotFoundException e) {
        System.out.println("Error: No se encontró el driver de MySQL.");
        e.printStackTrace();
        }
    }
    
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
        loadDriver();
        try (Connection conn = DriverManager.getConnection(URL, USER, PSWD)) {
            String query = "SELECT * FROM usuario WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); 
            System.out.println("Trying: " + username + " - " + password);  // DEBUG
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            System.out.println("User found: " + username);
            return true;
        } else {
            System.out.println("No user found.");
            return false;
        }
        } catch (Exception e) {
            System.out.println("Database Error: " + e);
            return false;
        }
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

