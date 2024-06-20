package Client;

import Shared.ChatMessage;
import Shared.MessageType;

public class SystemMessages {
    private static final String SYSTEM_USER = "System";

    public static final ChatMessage STARTUP_FAIL = new ChatMessage(SYSTEM_USER,
        "Welcome to the ChatServer."
        + "<br>Unfortunately there is an error occurred during application setup, "
        + "please make sure the server is properly configured and running.",
        MessageType.Error
    );
    public static final ChatMessage STARTUP_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Welcome to the ChatServer.<br>"
        + "Please register by entering a username in the inputfield at the top.",
        MessageType.Message
    );
    public static final ChatMessage REGISTERED_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Successfully registered on the server."
        + "<br>You can now send your messages. ✉",
        MessageType.Message
    );
    public static final ChatMessage REGISTERED_FAIL = new ChatMessage(SYSTEM_USER,
        "Failed registering on chat-server. Possible reasons:"
        + "<br>- Username is already taken or you entered the wrong password."
        + "<br>- Chat-Server is not responding or failed on registered the user.",
        MessageType.Error
    );
    public static final ChatMessage NO_CREDENTIALS_FAIL = new ChatMessage(SYSTEM_USER,
        "Registration aborted."
        + "<br>Please enter a username and password for registration.",
        MessageType.Error
    );
    public static final ChatMessage LOGOUT_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Successfully logged out."
        + "<br>Please register/login again to continue chatting.",
        MessageType.Message
    );
    public static final ChatMessage MESSAGE_SEND_FAIL = new ChatMessage(SYSTEM_USER,
        "Failed to send the last message. Please try again later.",
        MessageType.Error
    );
    public static final ChatMessage MESSAGE_GET_COUNTER_FAIL = new ChatMessage(SYSTEM_USER, 
        "Failed several times in a row to fetch the latest message."
        + "<br>Please check your connection to the server.", 
        MessageType.Error
    );
}
