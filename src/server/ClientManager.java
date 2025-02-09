package server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.InetAddress;


public class ClientManager {
    public InetAddress ip;
    public int port;
    public String username;
    public ConcurrentLinkedQueue<JSONObject> queue
            = new ConcurrentLinkedQueue<>();
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientManager(InetAddress ip, int port, String username,
                         Socket socket, DataInputStream dis,
                         DataOutputStream dos) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    public void run() {
        Thread senderThread = new Thread(() -> {
            try {
                sender();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread receiverThread = new Thread(this::receiver);
        senderThread.start();
        receiverThread.start();
    }

    public void receiver() {
        for (;;) {
            try {
                JSONObject received = new JSONObject(this.dis.readUTF());
                // FIXME no está recibiendo nada aquí
                Server.queue.add(received);
                System.out.println("Server received:\n" + received);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                System.err.println("Client sent a message with invalid JSON: "
                        + e);
            }
        }
    }

    public void sender() throws InterruptedException {
        for (;;) {
            Thread.sleep(50);

            JSONObject message = this.queue.poll();

            if (message == null)
                continue;

            System.out.println("Sending message\n" + message);
            try {
                this.dos.writeUTF(message.toString());
            } catch (IOException e) {
                System.err.println("Cannot send message\n: " + message);
                // Add the message back to the queue so we can try to send it
                // again
                this.queue.add(message);
            }
        }
    }
}
