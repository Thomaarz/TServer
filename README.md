<h1>TServer</h1>
An easy java api to communicate between differents programs using sockets.
Required org.projectlombok and commons-io dependency .


<h1>Server Class</h1>


```java
public class MyServer extends TServerSocket {

    public MyServer() {
        super(2222);
    }

    @Override
    public void onReceive(String client, String message) {
        String channel = TClientSocket.getChannel(message);
        String[] args = TClientSocket.getArgs(message);

        if (channel.equalsIgnoreCase("Test")) {
            if (args.length == 1) {
                System.out.println(client + " a envoyé le message " + args[0] + " !");
            }
        }
    }
}
```

<h1>Client Class</h1>

```java
public class MyClient extends TClientSocket { 

    public MyClient() {
        super("Client 1", 2222);
    }

    @Override
    public void onReceive(String message) {
        String channel = getChannel(message);
        String[] args = getArgs(message);
        
        if (channel.equalsIgnoreCase("Test")) {
            if (args.length == 1) {
                System.out.println("Le serveur vous a envoyé " + args[0]);
            }
        }
    }
}
```


<h1>Server Main</h1>

```java
public static void main(String[] args) {

    // Create the server
    MyServer myServer = new MyServer();

    // Send a message to "Client 1"
    myServer.sendMessage("Client 1", "Hello !");
    myServer.sendMessage("Client 1", 1);
    myServer.sendMessage("Client 1", true);

    // Send a message to ALL clients (ALL can be replaced by "*")
    myServer.sendMessage("ALL", "Hello everyone !");
}
```

<h1>Client Main</h1>

```java
public static void main(String[] args) {

    // Create the client
    MyClient myClient = new MyClient();

    // Send a message to the server
    myClient.sendMessage("Hey !");

    // Send a message to the server and return the value that you has define
    // in the server code (You have to define the value in the server class)
    int usersAmount = myClient.getInt("OnlineUsers");
    boolean newUser = myClient.getBoolean("NewUser");
    String welcomeMessage = myClient.getString("WelcomeMessage");

    // Send Message to client
    System.out.println("There are " + usersAmount + " users on the server");
    if (newUser) {
        System.out.println(welcomeMessage);
    }
}
```
