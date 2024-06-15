package Server;

import java.rmi.registry.LocateRegistry;

public class ChatServer {
    private static final int PORT = 1200;

    public static void main(String[] args) {
        System.out.println("Start server with port " + PORT);
        try {
            var registry = LocateRegistry.createRegistry(PORT);
            registry.bind("Chat", new ChatConnection());
        } catch (Exception e) {
            System.err.println("Error in Server: " + e.getMessage());
        }
    }
}
