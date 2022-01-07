package fr.thomarz;

import lombok.Getter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Getter
public abstract class TClientSocket {

    private String name;
    public int port;
    private BufferedWriter writer;
    private BufferedReader reader;

    public TClientSocket(String name, int port) {
        this(name, port, true);
    }

    public TClientSocket(String name, int port, boolean receive) {
        this.name = name;
        this.port = port;
        try {
            InetAddress serveur = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(serveur, port);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            connect(receive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
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

    private void connect(boolean receive) {
        sendMessage(getName());

        try {
            String message = reader.readLine();
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (receive) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String message = reader.readLine();
                            if (message == null) {
                                break;
                            }
                            onReceive(message);
                            System.out.println("Server -> " + message);
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String message) {
        TClientSocket lcClientSocket = new TClientSocket(name + new Random().nextInt(9999) + "Copy", port, false) {
            @Override
            public void onReceive(String message) {

            }
        };
        return lcClientSocket.getResult(message).toString();
    }

    public boolean getBoolean(String message) {
        TClientSocket lcClientSocket = new TClientSocket(name + new Random().nextInt(9999) + "Copy", port, false) {
            @Override
            public void onReceive(String message) {

            }
        };
        try {
            return (boolean) lcClientSocket.getResult(message);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int getInt(String message) {
        TClientSocket lcClientSocket = new TClientSocket(name + new Random().nextInt(9999) + "Copy", port, false) {
            @Override
            public void onReceive(String message) {

            }
        };
        try {
            return (int) lcClientSocket.getResult(message);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public Object getResult(String message) {
        sendMessage(message);
        try {
            String response = getReader().readLine();
            getReader().close();
            System.out.println("Server -> " + getName() + ": " + response);
            try {
                return Integer.parseInt(response);
            } catch (Exception e1) {
                try {
                    return Double.parseDouble(response);
                } catch (Exception e2) {
                    if (response != null && response.equalsIgnoreCase("true")) {
                        return true;
                    } else if (response != null && response.equalsIgnoreCase("false")) {
                        return false;
                    } else {
                        return response;
                    }
                }
            }
        } catch (IOException e) {
            return 0;
        }
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
}
