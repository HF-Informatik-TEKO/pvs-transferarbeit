package Client;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.awt.event.KeyAdapter;
import java.util.Date;
import java.util.List;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import Shared.ChatMessage;
import Shared.IChatConnection;

public class ChatClient {

    private static final int PORT = 1200;
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String RPC_NAME = "Chat";
    /** Delay between calls for new messages. */
    private static final int REFRESH_RATE_MS = 800;
    /** Delay between client history clean (reset to server max history and only server messages.). */
    private static final long RESET_RATE_MS = 60_000; // 10 Minutes

    private static final String HTML_HEADER = ""
        + "<head><style>"
            + "body { font-size: 12px; font-family: Consolas, sans-serif; color: white; }"
            + ".sender { color: gray; font-style: italic; font-size: 11px}"
            + ".message { color: green; }"
            + ".self { text-align: right; }"
            + ".error { color: red; }"
        + "</style></head>";

    private static String userName;
    private static String userToken;

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
        var window = getClientWindow(chat, errorMessage);
        window.setVisible(true);
    }

    private static JFrame getClientWindow(IChatConnection chat, String errorMessage) {
        var window = new JFrame(
            "RCP (RMI) - TCP, Chat Client"
            + " | PVS Transverarbeit | Beat Zimmermann"
            + " | Z-TIN-21-T-a | HF Informatik Appl."
        );
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);

        var windowBgColor = new Color(130, 150, 140); // gray background color
        var b = 3; // border-thickness

        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(b, b, b, b));
        mainPanel.setBackground(windowBgColor);

        // #region Registration
        var registrationPanel = new JPanel(new BorderLayout());
        registrationPanel.setBorder(new EmptyBorder(0, 0, b, 0));
        registrationPanel.setBackground(windowBgColor);

        var userAndPasswordPanel = new JPanel(new GridLayout(1, 2));

        var registrationUserName = new JTextField();
        registrationUserName.setToolTipText("Choose a user name to chat with others (visible).");
        userAndPasswordPanel.add(registrationUserName, BorderLayout.WEST);

        var registrationPassword = new JPasswordField();
        registrationPassword.setToolTipText("Choose a password, if the user doesn't exist. "
            + "Enter the correct password, if the user exists.");
        userAndPasswordPanel.add(registrationPassword, BorderLayout.CENTER);
        registrationPanel.add(userAndPasswordPanel);

        var registerButton = new JButton("Register");
        registrationPanel.add(registerButton, BorderLayout.EAST);
        // #endregion

        // #region Chat
        var editorPane = new JEditorPane();
        editorPane.setBackground(Color.BLACK);
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        var htmlDocument = new HTMLDocument();
        editorPane.setDocument(htmlDocument);
        if (chat == null) {
            displayMessage(editorPane, HTML_HEADER + SystemMessages.STARTUP_FAIL.toErrorMessageHtml() + "<div>" + errorMessage + "</div>");
        } else {
            displayMessage(editorPane, HTML_HEADER + SystemMessages.STARTUP_SUCCESS.toMessageHtml());
        }

        var scrollPane = new JScrollPane(editorPane);
        // #endregion

        // #region Message
        var messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder(3, 0, 0, 0));
        messagePanel.setBackground(windowBgColor);

        var chatInput = new JTextField(24);
        chatInput.setToolTipText("Write your oneline message here. (press ENTER to send)");
        chatInput.setEnabled(false);

        messagePanel.add(chatInput, BorderLayout.CENTER);

        var sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        messagePanel.add(sendButton, BorderLayout.EAST);
        // #endregion

        if (chat != null) {
        // #region ActionListeners
        registerButton.addActionListener(e -> {
            if (registerButton.getText().equals("Logout")) {
                System.out.println("logout user");
                userName = null;
                userToken = null;
                displayMessage(editorPane, SystemMessages.LOGOUT_SUCCESS.toMessageHtml());
                chatInput.setEnabled(false);
                sendButton.setEnabled(false);
                registrationUserName.setEditable(true);
                registrationPassword.setText("");
                registrationPassword.setEditable(true);
                registerButton.setText("Register");
                return;
            }

            System.out.println("register user");
            if (registrationUserName.getText().isBlank() || registrationPassword.getPassword().length < 1) {
                displayMessage(editorPane, SystemMessages.NO_CREDENTIALS_FAIL.toErrorMessageHtml());
                System.err.println("Unsufficient credentials for registration.");
                return;
            }

            String token = null;
            userName = registrationUserName.getText();
            try {
                token = chat.register(userName, new String(registrationPassword.getPassword()));
            } catch (RemoteException ex) {
                userName = null;
                System.err.println("Remote exception on user register: " + ex.getMessage());
            }
            if (token == null) {
                displayMessage(editorPane, SystemMessages.REGISTERED_FAIL.toErrorMessageHtml());
                System.err.println("Failed to get user token for user " + userName);
                return;
            }
            userToken = token;
            System.out.println("Successfully registered user, user token: " + userToken);
            displayMessage(editorPane, SystemMessages.REGISTERED_SUCCESS.toMessageHtml());
            chatInput.setEnabled(true);
            sendButton.setEnabled(true);
            registrationUserName.setEditable(false);
            registrationPassword.setText("");
            registrationPassword.setEditable(false);
            registerButton.setText("Logout");

        });

        sendButton.addActionListener(e -> {
            sendMessage(chat, chatInput);
        });

        chatInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(chat, chatInput);
                }
            }
        });
        // #endregion
        }

        mainPanel.add(registrationPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(messagePanel, BorderLayout.SOUTH);
        window.add(mainPanel);

        new Thread(() -> fetchAndDisplayMessages(chat, editorPane)).start();
        return window;
    }

    private static void displayMessage(JEditorPane editorPane, String html) {
        try {
            var htmlDocument = (HTMLDocument) editorPane.getDocument();
            var editorKit = (HTMLEditorKit) editorPane.getEditorKit();
            editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), html, 0, 0, null);
        } catch (Exception e) {
            // Ignore Exception.
        }
    }

    private static void sendMessage(IChatConnection chat, JTextField chatInput) {
        System.out.println("send message");
        try {
            chat.send(userToken, chatInput.getText());
            chatInput.setText("");
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    private static void fetchAndDisplayMessages(IChatConnection chat, JEditorPane editorPane) {
        var htmlDocument = (HTMLDocument) editorPane.getDocument();
        var editorKit = (HTMLEditorKit) editorPane.getEditorKit();
        var lastReset = System.currentTimeMillis();
        Date lastRecieved = null;

        while (true) {
            try {
                if (userToken == null) {
                    lastReset = System.currentTimeMillis();
                    continue;
                }

                if ((System.currentTimeMillis() - lastReset) > RESET_RATE_MS) {
                    lastRecieved = null;
                    editorPane.setText("");
                    lastReset = System.currentTimeMillis();
                }

                List<ChatMessage> fetchedMessages;
                try {
                    fetchedMessages = chat.get(userToken, lastRecieved);
                } catch (RemoteException e1) {
                    System.err.println("Failed to fetch new messages.");
                    continue;
                }

                if (fetchedMessages.size() > 0) {
                    System.out.println("recieved message(s) " + fetchedMessages.size());
                    var sb = new StringBuilder();
                    for (var m : fetchedMessages) {
                        sb.append(m.toMessageHtml(userName));
                    }
                    try {
                        editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), sb.toString(), 0, 0, null);
                        editorPane.setCaretPosition(htmlDocument.getLength());
                    } catch (Exception e) {
                        System.err.println("Failed to add messages to UI.");
                        continue;
                    }
                    lastRecieved = new Date();
                }

            } finally {
                try {
                    Thread.sleep(REFRESH_RATE_MS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
