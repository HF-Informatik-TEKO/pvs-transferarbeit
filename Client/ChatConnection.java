package Client;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import Client.Gui.ChatWindow;
import Shared.ChatMessage;
import Shared.IChatConnection;

public class ChatConnection {

    private final IChatConnection connection;
    private final ChatWindow gui;
    private final int MESSAGE_GET_MAX_COUNT = 10;

    private String userToken;
    private String userName;
    private int messageGetFailCounter;

    public ChatConnection(IChatConnection connection, ChatWindow gui) {
        this.connection = connection;
        this.gui = gui;
    }

    public String registerUser(String userName, String password) {
        String token = null;
        try {
            token = connection.register(userName, password);
            if (token != null) {
                userToken = token;
                this.userName = userName;
            }
        } catch (RemoteException e) {
            // Ignore Exception
        }

        return token;
    }

    public String sendMessage(String message) {
        System.out.println("send message");
        try {
            connection.send(userToken, message);
        } catch (RemoteException e1) {
            System.err.println("Failed to send messages.");
            gui.displayMessage(SystemMessages.MESSAGE_SEND_FAIL.toHtml());
        }

        return null;
    }

    public List<ChatMessage> getMessages(Date lastRecieved) {
        List<ChatMessage> fetchedMessages = new ArrayList<ChatMessage>();
        try {
            fetchedMessages = connection.get(userToken, lastRecieved);
            messageGetFailCounter = 0;
        } catch (RemoteException e) {
            System.err.println("Failed to fetch new messages.");
            messageGetFailCounter++;
        }
        
        if (fetchedMessages.size() > 0) {
            System.out.println("recieved message(s) " + fetchedMessages.size());
        }

        if (messageGetFailCounter > MESSAGE_GET_MAX_COUNT) {
            fetchedMessages.add(SystemMessages.MESSAGE_GET_COUNTER_FAIL);
            messageGetFailCounter = 0;
        }

        return fetchedMessages;
    }
        
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isGetFailTimeOut() {
        return messageGetFailCounter > 1;
    }
}
