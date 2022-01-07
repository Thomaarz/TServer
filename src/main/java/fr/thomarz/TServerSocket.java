package fr.thomarz;

import lombok.Getter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class TServerSocket {

    public final Map<String, Socket> clients = new HashMap<>();

    private String name;
    public int port;
    private ServerSocket serverSocket;

    public TServerSocket(int port) {
        this.name = "LC Server";
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(name + " Connecté");
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch();
    }

    private void launch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket client = serverSocket.accept();
                        runClient(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String client, Object object) {
        sendMessage(client, object.toString());
    }

    public void sendMessage(String client, String message) {
        if (client.equalsIgnoreCase("*") || client.equalsIgnoreCase("ALL")) {
            for (Socket socket : clients.values()) {
                if (socket == null) {
                    continue;
                }
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                } catch (IOException ignored) {

                }
            }
        } else {
            Socket socket = clients.get(client);
            if (socket == null) {
                return;
            }
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException ignored) {

            }
        }
    }

    private void runClient(Socket client) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));

            final String clientName = reader.readLine();
            clients.put(clientName, client);
            System.out.println(clientName + " has connected");
            sendMessage(clientName, "connect to " + name);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String message = reader.readLine();
                            if (message == null) {
                                System.out.println(clientName + " has disconnected");
                                clients.remove(clientName);
                                break;
                            }
                            System.out.println(clientName + " send message:" + message);
                            onReceive(clientName, message);

                            if (clientName.endsWith("Copy")) {
                                System.out.println(clientName + " has disconnected");
                                clients.remove(clientName);
                                break;
                            }

                            if (message.equals("Disconnect")) {
                                System.out.println(clientName + " has disconnected");
                                clients.remove(clientName);
                                reader.close();
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println(clientName + " has disconnected");
                            clients.remove(clientName);
                            break;
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onReceive(String client, String message);
}
