package Client;

import java.rmi.Naming;
import Client.Gui.ChatWindow;
import Shared.IChatConnection;

public class ChatClient {

    private static final int PORT = 1200;
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String RPC_NAME = "Chat";

    public static void main(String[] args) {
        IChatConnection chat = null;
        String errorMessage = null;
        String connection = String.format("rmi://%s:%d/%s", IP_ADDRESS, PORT, RPC_NAME);
        
        try {
            chat = (IChatConnection) Naming.lookup(connection);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = ""
            + "Type:\n->" + e.getClass().getSimpleName()
            + "<br>Connection:\n->" + connection
            + "<br>Message:\n->" + e.getMessage();
        }

        var window = new ChatWindow(chat, errorMessage, 500, 60_000);
        window.setVisible(true);
    }
}
