package Client;

import java.rmi.Naming;
import Client.Gui.ChatWindow;
import Shared.IChatConnection;

public class ChatClient {

    private static final int PORT = 1200;
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String RPC_NAME = "Chat";
    /** Delay between calls for new messages. */
    private static final int REFRESH_RATE_MS = 500;
    /** Delay between client history clean (reset to server max history and only server messages). */
    private static final long RESET_RATE_MS = 600_000; // 10 Minutes
    /** Tries before display a error message on failed message fetch. */
    private static final int MESSAGE_GET_MAX_COUNT = 10;

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

        var window = new ChatWindow(
            chat, 
            errorMessage, 
            REFRESH_RATE_MS,
            RESET_RATE_MS,
            MESSAGE_GET_MAX_COUNT
        );
        window.setVisible(true);
    }
}
