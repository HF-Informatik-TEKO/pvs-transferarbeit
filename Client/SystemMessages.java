package Client;

import Server.Models.ChatMessage;

public class SystemMessages {
    public static final ChatMessage STARTUP_FAIL_MESSAGE = new ChatMessage("System",
        "Welcome to the ChatServer.<br>"
        + "Unfortunately there is an error occurred during application setup, "
        + "please make sure the server is properly configured and running.");
    public static final ChatMessage STARTUP_SUCCESS_MESSAGE = new ChatMessage("System",
        "Welcome to the ChatServer.<br>"
        + "Please register by entering a username in the inputfield at the top.");
    public static final ChatMessage REGISTERED_SUCCESS_MESSAGE = new ChatMessage("System",
        "Successfully registered on the server.<br>"
        + "You can now send your messages. ✉");
    public static final ChatMessage REGISTERED_FAIL_MESSAGE = new ChatMessage("System",
        "Failed registering on the server."
        + "Please restart the application and try again later.");
}
