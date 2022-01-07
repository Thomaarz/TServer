<h1>TServer</h1>
An easy java api to communicate between differents programs using sockets


<h1>Server Usage</h1>


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
                System.out.println(client + " a envoyé le message " + args[0] + " !")
            }
        }
    }
}
```

<h1>Client Usage</h1>

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
