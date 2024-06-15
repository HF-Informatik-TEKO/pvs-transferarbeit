package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Server.Models.ChatMessage;
import Shared.IChatConnection;

public class ChatConnection
        extends UnicastRemoteObject
        implements IChatConnection 
{
    private final int MAX_MESSAGE_HISTORY = 500;
    private final String CLIENT_NOT_REGISTERED_MESSAGE = 
        "Sorry no client regiestered. Please register the client first.";
    private final ArrayList<ChatMessage> CLIENT_NOT_REGISTERED_LIST = new ArrayList<>();

    private final MySemaphore clientSemaphore = new MySemaphore(10);
    private final HashMap<String, String> clients = new HashMap<>(); // key = token, value = name
    private final MySemaphore messageSemaphore = new MySemaphore(10);
    private final ArrayList<ChatMessage> messages = new ArrayList<>();

    protected ChatConnection() throws RemoteException {
        super();
        CLIENT_NOT_REGISTERED_LIST.add(
            new ChatMessage("System", CLIENT_NOT_REGISTERED_MESSAGE));
    }

    @Override
    public String register(String name) throws RemoteException {
        var t = new Thread(() -> {
            clientSemaphore.passeren(1);
            var isKeyPresent = clients.containsValue(name);
            clientSemaphore.vrijgave(1);
            if (isKeyPresent) {
                return;
            }

            var uid = UUID.randomUUID().toString();
            clientSemaphore.passeren(10);
            clients.put(uid, name);
            clientSemaphore.vrijgave(10);
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            return null;
        }

        return getClientToken(name);
    }

    @Override
    public String send(String userToken, String message) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return CLIENT_NOT_REGISTERED_MESSAGE;
        }

        var t = new Thread(() -> {
            var m = new ChatMessage(clients.get(userToken), message);

            messageSemaphore.passeren(10);
            messages.add(m);

            if (messages.size() > MAX_MESSAGE_HISTORY) {
                messages.remove(0);
            }
            messageSemaphore.vrijgave(10);
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "successfully sendt message to server. " + message;
    }

    @Override
    public List<ChatMessage> get(String userToken, Date dat) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return CLIENT_NOT_REGISTERED_LIST;
        }

        if (dat == null) {
            return messages;
        }

        // Filter messages by date.
        return messages.stream().filter(e -> e.getTime().compareTo(dat) > 0).toList();
    }

    private boolean isClientRegistered(String userToken) {
        return clients.containsKey(userToken);
    }

    private String getClientToken(String name) {
        for (var entry : clients.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
