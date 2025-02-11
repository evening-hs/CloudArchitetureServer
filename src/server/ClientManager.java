package server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.InetAddress;


public class ClientManager extends Thread {
    // Maximum number of messages in the queue before the client is
    // considered unresponsive and the connection is closed
    private static final int MAX_QUEUED_MESSAGES = 10;

    private int CLIENT_STATUS;

    public InetAddress ip;
    public int port;
    public String username;
    public ConcurrentLinkedQueue<JSONObject> queue
            = new ConcurrentLinkedQueue<>();
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Thread senderThread;
    private Thread receiverThread;

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

    @Override
    public void run() {
        this.senderThread = new Thread(() -> {
            try {
                sender();
            } catch (IOException e) {
                closeConnection();
            }
        });
        this.receiverThread = new Thread(() -> {
            try {
                receiver();
            } catch (IOException e) {
                closeConnection();
            }
        });
        this.senderThread.start();
        this.receiverThread.start();
    }

    public void receiver() throws IOException {
        for (;;) {
            try {
                JSONObject received = new JSONObject(this.dis.readUTF());
                Server.queue.add(received);
                System.out.println("Server received:\n" + received);
            } catch (JSONException e) {
                System.err.println("Ignoring message because it has invalid " +
                        "JSON: " + e);
            }
        }
    }

    public void sender() throws IOException {
        for (;;) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println(e);
            }

            JSONObject message = this.queue.poll();

            if (message == null)
                continue;

            System.out.println("Sending message\n" + message);
            this.dos.writeUTF(message.toString());
        }
    }

    /**
     * Closes the data streams and the socket causing exceptions in all other
     * threads which indicates them to stop.
     */
    public void closeConnection() {
        try {
            this.dis.close();
        } catch (IOException e) {
            System.err.println("Cannot close DataInputStream gracefully");
        }
        try {
            this.dos.close();
        } catch (IOException e) {
            System.err.println("Cannot close DataOutputStream gracefully");
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            System.err.println("Cannot close socket gracefully");
        }

        this.senderThread.interrupt();
        this.receiverThread.interrupt();
        this.interrupt();
    }
}

