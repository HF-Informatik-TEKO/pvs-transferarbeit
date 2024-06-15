package Client;

import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.border.*;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.awt.event.KeyAdapter;

import Server.Models.*;
import java.util.*;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import Shared.IChatConnection;

public class ChatClient {

    private static String userName;
    private static String userToken;

    /** Delay between calls for new messages. */
    private static final int REFRESH_RATE_MS = 800;
    /** Delay between client history clean (reset to server max history). */
    private static final long RESET_RATE_MS = 600_000; // 10 Minutes
    private static final ChatMessage GREETINGS_MESSAGE = new ChatMessage("System",
            "Welcome to the ChatServer.<br>"
                    + "Please register first by entering a username in the inputfield at the top.");
    private static final ChatMessage REGISTERED_SUCCESS_MESSAGE = new ChatMessage("System",
            "Successfully registered on the server.<br>"
                    + "You can now send your messages. ✉");
    private static final ChatMessage REGISTERED_FAIL_MESSAGE = new ChatMessage("System",
            "Failed registering on the server."
                    + "Please restart the application and try again later.");
    private static final String HTML_HEADER = "<head><style>"
            + "body { font-size: 12px; font-family: Consolas, sans-serif; }"
            + ".sender { color: gray; font-style: italic; font-size: 11px}"
            + ".message { color: green; }"
            + ".self { text-align: right; }"
            + "</style></head>";

    public static void main(String[] args) {
        try {
            var chat = (IChatConnection) Naming.lookup("rmi://127.0.0.1:1200/Chat");
            var window = getClientWindow(chat);
            window.setVisible(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static JFrame getClientWindow(IChatConnection chat) {
        var window = new JFrame("My Window");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);

        var windowBgColor = new Color(100, 100, 100);
        var borderThikness = 3;

        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(borderThikness, borderThikness, borderThikness, borderThikness));
        mainPanel.setBackground(windowBgColor);

        // #region Registration
        var registrationPanel = new JPanel(new BorderLayout());
        registrationPanel.setBorder(new EmptyBorder(0, 0, borderThikness, 0));
        registrationPanel.setBackground(windowBgColor);

        var registrationInput = new JTextField(21);
        registrationInput.setToolTipText("Choose a user name to chat with others (visible).");
        registrationPanel.add(registrationInput, BorderLayout.CENTER);

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
        displayMessage(editorPane, HTML_HEADER + GREETINGS_MESSAGE.toHtmlString());

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

        // #region ActionListeners
        registerButton.addActionListener(e -> {
            System.out.println("register user");
            userName = registrationInput.getText();
            try {
                userToken = chat.register(userName);
                if (userToken == null) {
                    displayMessage(editorPane, REGISTERED_FAIL_MESSAGE.toHtmlString());
                    System.err.println("Failed to get user token for user " + userName);
                    return;
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
                return;
            }
            System.out.println("Successfully registered user, user token: " + userToken);
            displayMessage(editorPane, REGISTERED_SUCCESS_MESSAGE.toHtmlString());
            chatInput.setEnabled(true);
            sendButton.setEnabled(true);
            registerButton.setEnabled(false);
            registrationInput.setEditable(false);
            // Thread.sleep(5_000);
            // editorPane.setText("");
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
        var lastRecieved = new Date();
        var lastReset = System.currentTimeMillis();

        while (true) {
            try {
                if (userToken == null) {
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
                        sb.append(m.toHtmlString(userName));
                    }
                    try {
                        editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), sb.toString(), 0, 0, null);
                        editorPane.setCaretPosition(htmlDocument.getLength());
                    } catch (Exception e) {
                        System.err.println("Failed to add messages to UI.");
                        continue;
                    }
                }

                lastRecieved = new Date();
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
