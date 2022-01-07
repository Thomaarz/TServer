
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

        if (channel.equalsIgnoredCas("Test")) {
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
        Guild guild = LacraftiaMinecraft.getLacraftia();
        if (guild == null) {
            return;
        }
        
    }
    
}
```
