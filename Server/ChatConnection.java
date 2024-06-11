package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import Server.Models.ChatMessage;
import Server.Models.Return;
import Shared.IChatConnection;

public class ChatConnection 
    extends UnicastRemoteObject
    implements IChatConnection
{
    private final String CLIENT_NOT_REGISTERED = "Sorry no client regiestered. Please register the client first.";

    private MySemaphore clientSemaphore = new MySemaphore(10);
    private HashMap<String, String> clients = new HashMap<>(); // key = token, value = name
    private MySemaphore messageSemaphore = new MySemaphore(10);
    private ArrayList<ChatMessage> messages = new ArrayList<>();

    protected ChatConnection() throws RemoteException {
        super();
    }

    private boolean isClientRegistered(String userToken) {
        return clients.containsKey(userToken);
    }

    @Override
    public String send(String userToken, String message) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return CLIENT_NOT_REGISTERED;
        }

        var r = new Return<ChatMessage>();
        var t = new Thread(() -> {
            var m = new ChatMessage();
            m.time = new Date();
            m.user = clients.get(userToken);
            m.message = message;

            r.value = m;

            messageSemaphore.passeren(10);
            messages.add(m);
            messageSemaphore.vrijgave(10);
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "successfully sendt message to server. " + r.value.toString();
    }

    @Override
    public String register(String name) throws RemoteException {
        var r = new Return<String>();
        var t = new Thread(() -> {
            clientSemaphore.passeren(1);
            var isKeyPresent = clients.containsValue(name);
            clientSemaphore.vrijgave(1);
            if (isKeyPresent) {
                return;
            }
            
            var uid = UUID.randomUUID().toString();
            r.value = uid;
            clientSemaphore.passeren(10);
            clients.put(uid, name);
            clientSemaphore.vrijgave(10);
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (r.value == null) {
            return getClientToken(name);
            // throw new RemoteException("Client is already registered. " + name);
        }
        return r.value;
    }
    
    private String getClientToken(String name) {
        for (var entry : clients.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public List<ChatMessage> get(String userToken, Date dat) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return new ArrayList<ChatMessage>(); 
        }

        if (dat == null) {
            return messages;
        }

        return messages.stream().filter(e -> e.time.compareTo(dat) > 0).toList();
    }
}
