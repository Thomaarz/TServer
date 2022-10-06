package fr.thomarz;

import lombok.Getter;

import java.io.*;
import java.net.InetAddress;
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

    public TServerSocket(String name, int port, InetAddress ip) {
        this.name = name;
        try {
            serverSocket = new ServerSocket(port, 50, ip);
            System.out.println(name + " Connecté");
        } catch (IOException e) {
            onStop();
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
                        onStop();
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
            sendMessage(socket, message);
        }
    }

    public void sendMessage(Socket socket, String message) {
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

    private void runClient(final Socket client) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

            final String clientName = reader.readLine();
            if (clients.containsKey(clientName)) {
                sendMessage(client, "Leave Ce pseudo est déjà utilisé.");
                return;
            }
            if (clientName.length() < 3) {
                sendMessage(client, "Leave Pseudo trop court.");
                return;
            }
            clients.put(clientName, client);
            onJoin(clientName);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String message = reader.readLine();

                            if (message == null) {
                                onQuit(clientName);
                                clients.remove(clientName);
                                break;
                            }

                            System.out.println(clientName + ": " + message);
                            onReceive(clientName, message);

                        } catch (Exception e) {
                            onQuit(clientName);
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

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }

    public abstract void onReceive(String client, String message);

    public void onJoin(String clientName) {
        System.out.println(clientName + " Connected");
    }

    public void onQuit(String clientName) {
        System.out.println(clientName + " Disconnected");
        clients.remove(clientName);
    }

    public void onStop() {}
}
