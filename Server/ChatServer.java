package Server;

import java.rmi.registry.LocateRegistry;

public class ChatServer {
    private static final int PORT = 1200;
    private static final String RPC_NAME = "Chat";
    private static final int MAX_MESSAGE_HISTORY = 500;

    public static void main(String[] args) {
        System.out.println("Start server with port " + PORT);
        try {
            var registry = LocateRegistry.createRegistry(PORT);
            registry.bind(RPC_NAME, new ChatConnection(MAX_MESSAGE_HISTORY));
        } catch (Exception e) {
            System.err.println("Error in Server: " + e.getMessage());
        }
    }
}
