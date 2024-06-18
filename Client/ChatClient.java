package Client;

import java.rmi.Naming;
import Client.Gui.ChatWindow;
import Shared.AppSettingsReader;
import Shared.IChatConnection;

public class ChatClient {

    public static void main(String[] args) {
        var appSettings = AppSettingsReader.read(args);

        IChatConnection chat = null;
        String errorMessage = null;
        String connection = String.format("rmi://%s:%d/%s", appSettings.ipAddress, appSettings.port, appSettings.rpcName);
    
        try {
            chat = (IChatConnection) Naming.lookup(connection);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = ""
            + "Type:\n->" + e.getClass().getSimpleName()
            + "<br>Connection:\n->" + connection
            + "<br>Message:\n->" + e.getMessage();
        }
    
        var window = new ChatWindow(chat, errorMessage, appSettings);
        window.setVisible(true);
    }
}
