package Server;

import java.rmi.registry.LocateRegistry;

import Shared.AppSettingsReader;

public class ChatServer {

    public static void main(String[] args) {
        var appSettings = AppSettingsReader.read(args);

        System.out.println("Start server with port " + appSettings.port);
        try {
            var registry = LocateRegistry.createRegistry(appSettings.port);
            registry.bind(appSettings.rpcName, new ChatConnection(appSettings.serverMaxMessageHistory));
        } catch (Exception e) {
            System.err.println("Error in Server: " + e.getMessage());
        }
    }

}
