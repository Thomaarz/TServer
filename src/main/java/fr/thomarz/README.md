
<h1>Server Usage</h1>


```

public void MyServer() {
    super(2222);
}

@Override
public void onReceive(String client, String message) {
    String channel = LCClientSocket.getChannel(message);
    String[] args = LCClientSocket.getArgs(message);

    if (channel.equalsIgnoredCas("Test")) {
        if (args.length == 1) {
            System.out.println(client + " a envoyé le message " + args[0] + " !")
        }
        
    }

}
```
