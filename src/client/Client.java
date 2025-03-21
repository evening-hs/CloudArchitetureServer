package client;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static String serverIP = "localhost";
    public static int serverPort = 2555;
    public static Socket socket = null;
    public static DataInputStream dis = null;
    public static DataOutputStream dos = null;
    public static Scanner input = null;
    public static String username = null;
    public static Thread senderThread = null;
    public static Thread receiverThread = null;

    public static void main(String[] args) {
        input = new Scanner(System.in);

        if (args.length > 3) {
            System.err.println("Usage: java Client <username> " +
                    "<server ip>? <server port>?");
            System.exit(1);
        }

        if (args.length >= 1) {
            username = args[0];
        } else {
            System.out.print("Username: ");
            username = input.nextLine();
        }

        if (args.length >= 2) {
            serverIP = args[1];
        }
        if (args.length == 3) {
            serverPort = Integer.parseInt(args[2]);
        }

        try {
            System.out.println("Connecting to " + serverIP + ":" + serverPort);
            InetAddress address = InetAddress.getByName(serverIP);
            socket = new Socket(address, serverPort);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected");

            if (!login()) {
                System.out.println("Login failed. Exiting...");
                closeConnection();
                System.exit(0);
            }

            // main thread
            senderThread = new Thread(Client::sender);
            receiverThread = new Thread(Client::receiver);

            senderThread.start();
            receiverThread.start();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            closeConnection();
        }
    }

    public static boolean login() throws IOException {
        int remainingAttempts = 3;
        while (true) {
            // Send login info
            System.out.print("Enter password: ");
            String password = input.nextLine();

            JSONObject message = new JSONObject();
            message.put("username", username);
            message.put("password", password);
            message.put("command", "login");

            dos.writeUTF(message.toString());
            dos.flush();

            // wait for confirmation
            JSONObject response = new JSONObject(dis.readUTF());

            if (response.getString("command").equals("ok")) {
                System.out.println("Login successful");
                return true;
            } else {
                System.err.println("Login failed");
                System.err.println(response.getString("message"));
                remainingAttempts --;

                if (remainingAttempts == 0) {
                    return false;
                }
            }
        }
    }

    public static void sender() {
        for (;;) {
            System.out.print("Write a message: ");
            String command = input.nextLine();

            JSONObject message = new JSONObject();
            message.put("command", command);
            message.put("username", username);

            try {
                dos.writeUTF(message.toString());
                dos.flush();

                System.out.println("Sent message: " + message);

                if (message.getString("command").equals("exit")) {
                    closeConnection();
                    return;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void receiver() {
        for (;;) {
            try {
                JSONObject message = new JSONObject(dis.readUTF());
                System.out.println("\nReceived message: " + message);

                if (message.getString("command").equals("exit")
                    && message.getString("username").equals(username)) {
                    closeConnection();
                    return;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket");
        }

        try {
            if (dis != null) {
                dis.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing DataInputStream");
        }

        try {
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing DataOutputStream");
        }

        if (senderThread != null) {
            senderThread.interrupt();
        }

        if (receiverThread != null) {
            receiverThread.interrupt();
        }
    }
}
