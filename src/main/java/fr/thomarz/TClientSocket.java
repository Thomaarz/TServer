package fr.thomarz;

import lombok.Getter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Getter
public abstract class TClientSocket {

    private String name;
    public int port;
    private InetAddress server;
    private BufferedWriter writer;
    private BufferedReader reader;

    private Socket socket;

    public TClientSocket(String name, int port) throws UnknownHostException {
        this(name, port, InetAddress.getByName("127.0.0.1"));
    }

    public TClientSocket(String name, int port, InetAddress server) {
        this.name = name;
        this.port = port;
        this.server = server;
        try {
            this.socket = new Socket(server, port);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        if (writer != null) {
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect() {
        sendMessage(getName());

        try {
            String message = reader.readLine();
            System.out.println(message);
        } catch (IOException e) {

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = reader.readLine();
                        if (message != null) {
                            onReceive(message);
                            System.out.println("Receive message from server: " + message);
                        }
                    } catch (Exception e) {
                        System.out.println("Disconnected from server.");
                        break;
                    }
                }
            }
        }).start();
    }

    public static String getChannel(String message) {
        if (message == null) {
            return "";
        }
        if (!message.contains(" ")) {
            return message;
        }
        return message.split(" ")[0];
    }

    public static String[] getArgs(String message) {
        if (message == null) {
            return new String[0];
        }
        message = message.replaceFirst(getChannel(message) + " ", "").replaceFirst(getChannel(message), "");
        if (message.equalsIgnoreCase(" ") || message.equalsIgnoreCase("")) {
            return new String[0];
        }
        return message.split(" ");
    }

    public abstract void onReceive(String message);

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {

        }
    }
}
