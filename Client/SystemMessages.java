package Client;

import Shared.ChatMessage;

public class SystemMessages {
    private static final String SYSTEM_USER = "System";

    public static final ChatMessage STARTUP_FAIL = new ChatMessage(SYSTEM_USER,
        "Welcome to the ChatServer."
        + "<br>Unfortunately there is an error occurred during application setup, "
        + "please make sure the server is properly configured and running."
    );
    public static final ChatMessage STARTUP_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Welcome to the ChatServer.<br>"
        + "Please register by entering a username in the inputfield at the top."
    );
    public static final ChatMessage REGISTERED_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Successfully registered on the server."
        + "<br>You can now send your messages. ✉"
    );
    public static final ChatMessage REGISTERED_FAIL = new ChatMessage(SYSTEM_USER,
        "Failed registering on chat-server. Possible reasons:"
        + "<br>- Username is already taken or you entered the wrong password."
        + "<br>- Chat-Server is not responding or failed on registered the user."
    );
    public static final ChatMessage NO_CREDENTIALS_FAIL = new ChatMessage(SYSTEM_USER,
        "Registration aborted."
        + "<br>Please enter a username and password for registration."
    );
    public static final ChatMessage LOGOUT_SUCCESS = new ChatMessage(SYSTEM_USER,
        "Successfully logged out."
        + "<br>Please register/login again to continue chatting."
    );
}
