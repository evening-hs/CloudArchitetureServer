/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author uriel
 */
public class Server {

    public static ConcurrentLinkedQueue<JSONObject> queue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ClientManager> connectedClients;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Server starting...");
    }

    public static Boolean authenticate(String username, String password) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void multicast() throws InterruptedException {
        for (;;) {
            Thread.sleep(50);

            JSONObject message = queue.poll();

            if (message == null)
                continue;

            for (ClientManager client : connectedClients) {
                if (!client.username.equals(message.getString("username"))) {
                    client.queue.add(message);
                }
            }
        }
    }
}
