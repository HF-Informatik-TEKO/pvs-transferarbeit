package Client.Gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Client.ChatConnection;
import Client.SystemMessages;

public class RegistrationJPanel extends JPanel {
    
    private final ChatWindow WINDOW;
    private final ChatConnection CHAT;
    private final ActionListener alRegisterButton;
    private final ActionListener alLogoutButton;
    private final String REGISTER = "Register";
    private final String LOGOUT = "Logout";
    
    private JButton button;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private String userName;
    private String userToken;

    public RegistrationJPanel(ChatWindow window, ChatConnection chat) {
        WINDOW = window;
        CHAT = chat;

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(0, 0, WINDOW.BORDER_THICKNESS, 0));
        this.setBackground(WINDOW.BACKGROUND_COLOR);

        var userAndPasswordPanel = new JPanel(new GridLayout(1, 2));

        userNameField = new JTextField();
        userNameField.setToolTipText("Choose a user name to chat with others (visible).");
        userAndPasswordPanel.add(userNameField, BorderLayout.WEST);

        passwordField = new JPasswordField();
        passwordField.setToolTipText("Choose a password, if the user doesn't exist. "
            + "Enter the correct password, if the user exists.");
        userAndPasswordPanel.add(passwordField, BorderLayout.CENTER);
        this.add(userAndPasswordPanel);

        button = new JButton(REGISTER);
        this.add(button, BorderLayout.EAST);

        alRegisterButton = getRegisterListener();
        alLogoutButton = getLogoutListener();
    }

    @Override
    public void enable() {
        userNameField.setEditable(true);
        passwordField.setText("");
        passwordField.setEditable(true);
    }
    
    @Override
    public void disable() {
        button.removeActionListener(alLogoutButton);
        button.removeActionListener(alRegisterButton);
        button.setEnabled(false);
        userNameField.setEditable(false);
        passwordField.setText("");
        passwordField.setEditable(false);
    }
    
    public void enterLogoutMode() {
        button.setText(LOGOUT);
        disable();
        button.setEnabled(true);
        button.removeActionListener(alRegisterButton);
        button.addActionListener(alLogoutButton);
    }
    
    public void enterRegisterMode() {
        button.setText(REGISTER);
        enable();
        button.removeActionListener(alLogoutButton);
        button.addActionListener(alRegisterButton);
    }
    
    public String getUserName() {
        return userName;
    }

    public String getUserToken() {
        return userToken;
    }
    
    private ActionListener getLogoutListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logoutUser();
            }
        };
    }

    private ActionListener getRegisterListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryRegisterUser();
            }
        };
    }
    
    private void logoutUser() {
        System.out.println("logout user");
        userName = null;
        userToken = null;
        WINDOW.clearMessages();
        WINDOW.displayMessage(SystemMessages.LOGOUT_SUCCESS.toHtml());
        WINDOW.enterRegistrationMode();
    }

    private void tryRegisterUser() {
        System.out.println("register user");
        if (userNameField.getText().isBlank() || passwordField.getPassword().length < 1) {
            WINDOW.displayMessage(SystemMessages.NO_CREDENTIALS_FAIL.toHtml());
            System.err.println("Unsufficient credentials for registration.");
            return;
        }

        userName = userNameField.getText();
        var token = CHAT.registerUser(userName, new String(passwordField.getPassword()));
        userToken = token;
        if (userToken == null) {
            userName = null;
            WINDOW.displayMessage(SystemMessages.REGISTERED_FAIL.toHtml());
            System.err.println("Failed to get user token for user " + userName);
            return;
        }
        System.out.println("Successfully registered user, user token: " + userToken);
        WINDOW.displayMessage(SystemMessages.REGISTERED_SUCCESS.toHtml());
        WINDOW.enterMessageMode();
        WINDOW.startUpdaterTask();
    }
}
