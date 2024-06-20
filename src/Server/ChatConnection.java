package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Shared.ChatMessage;
import Shared.IChatConnection;
import Shared.MessageType;

public class ChatConnection
    extends UnicastRemoteObject
    implements IChatConnection 
{
    private final int MAX_MESSAGE_HISTORY;
    private final String CLIENT_NOT_REGISTERED_MESSAGE;
    private final List<ChatMessage> CLIENT_NOT_REGISTERED_LIST;

    private final MySemaphore clientSemaphore = new MySemaphore(10);
    private final HashMap<String, UserCredentials> clients = new HashMap<>();
    private final MySemaphore messageSemaphore = new MySemaphore(10);
    private final List<ChatMessage> messages = new ArrayList<>();

    protected ChatConnection(int maxMessageHistory) throws RemoteException {
        super();
        MAX_MESSAGE_HISTORY = maxMessageHistory;
        CLIENT_NOT_REGISTERED_MESSAGE = 
            "Sorry no client regiestered. Please register the client first.";
        CLIENT_NOT_REGISTERED_LIST = new ArrayList<>();
        CLIENT_NOT_REGISTERED_LIST.add(
            new ChatMessage("System", CLIENT_NOT_REGISTERED_MESSAGE, MessageType.Error));
    }

    @Override
    public String register(String name, String password) throws RemoteException {
        var credentials = new UserCredentials(name, password);

        clientSemaphore.passeren(1);
        var isUserNamePresent = isUserNameTaken(name);
        clientSemaphore.vrijgave(1);

        if (!isUserNamePresent) {
            var uid = UUID.randomUUID().toString();
            clientSemaphore.passeren(10);
            clients.put(uid, credentials);
            clientSemaphore.vrijgave(10);
        }

        return getClientToken(credentials);
    }

    @Override
    public String send(String userToken, String message) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return CLIENT_NOT_REGISTERED_MESSAGE;
        }
        var m = new ChatMessage(clients.get(userToken).getUserName(), message, MessageType.Message);

        messageSemaphore.passeren(10);
        messages.add(m);
        if (messages.size() > MAX_MESSAGE_HISTORY) {
            messages.remove(0);
        }
        messageSemaphore.vrijgave(10);

        return "successfully sendt message to server. " + message;
    }

    @Override
    public List<ChatMessage> get(String userToken, Date filterDate) throws RemoteException {
        if (!isClientRegistered(userToken)) {
            return CLIENT_NOT_REGISTERED_LIST;
        }

        List<ChatMessage> response;

        messageSemaphore.passeren(1);
        if (filterDate == null) {
            response = messages;
        } else {
            response = messages.stream()
                .filter(e -> e.getTime().compareTo(filterDate) > 0)
                .toList();
        }
        messageSemaphore.vrijgave(1);

        return response;
    }

    private boolean isClientRegistered(String userToken) {
        return clients.containsKey(userToken);
    }

    private boolean isUserNameTaken(String userName) {
        for (var entry : clients.entrySet()) {
            if (entry.getValue().getUserName().equals(userName)) {
                return true;
            }
        }

        return false;
    }

    private String getClientToken(UserCredentials credentials) {
        for (var entry : clients.entrySet()) {
            if (entry.getValue().equals(credentials)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
