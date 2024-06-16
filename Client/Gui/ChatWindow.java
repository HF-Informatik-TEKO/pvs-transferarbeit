package Client.Gui;

import Shared.IChatConnection;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import Client.ChatConnection;
import Client.GuiUpdater;
import Client.SystemMessages;

public class ChatWindow extends JFrame{

    public final Color BACKGROUND_COLOR = new Color(130, 150, 140);
    public final int BORDER_THICKNESS = 3;
    
    private final ChatConnection CHAT;
    private final long RESET_RATE_MS;
    private final int REFRESH_RATE_MS;

    private RegistrationJPanel registration;
    private MessageDisplayJPanel displayMessage;
    private MessageSendJPanel sendMessage;
    private GuiUpdater updaterTask;

    public ChatWindow(
        IChatConnection chat, 
        String errorMessage, 
        int refreshRate, 
        long resetRate, 
        int maxTimeouts
        ) 
    {
        this.CHAT = new ChatConnection(chat, this, maxTimeouts);
        this.REFRESH_RATE_MS = refreshRate;
        this.RESET_RATE_MS = resetRate;
        
        this.setTitle(
            "RCP (RMI) - TCP, Chat Client"
            + " | PVS Transverarbeit | Beat Zimmermann"
            + " | Z-TIN-21-T-a | HF Informatik Appl."
        );
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(
            BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        registration = new RegistrationJPanel(this, CHAT);
        displayMessage = new MessageDisplayJPanel();
        var messageScrollPane = new JScrollPane(displayMessage);
        sendMessage = new MessageSendJPanel(this, CHAT);

        if (chat == null) {
            enterAllDisabledMode();
            displayMessage(SystemMessages.STARTUP_FAIL.toHtml() 
                + "<div>" + errorMessage + "</div>");
        } else {
            enterRegistrationMode();
            displayMessage(SystemMessages.STARTUP_SUCCESS.toHtml());
        }

        mainPanel.add(registration, BorderLayout.NORTH);
        mainPanel.add(messageScrollPane, BorderLayout.CENTER);
        mainPanel.add(sendMessage, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    public void enterRegistrationMode() {
        registration.enterRegisterMode();
        sendMessage.disable();
    }
    
    public void enterMessageMode() {
        registration.enterLogoutMode();
        sendMessage.enable();
        sendMessage.requestFocus();
    }

    public void enterAllDisabledMode() {
        registration.disable();
        sendMessage.disable();
    }

    public void displayMessage(String html) {
        displayMessage.tryAppendMessage(html);
    }

    public void clearMessages() {
        displayMessage.resetDisplay();
    }

    public void startUpdaterTask() 
    {
        if (updaterTask != null) {
            updaterTask.isRunning = false;
        }
        updaterTask = new GuiUpdater(
            REFRESH_RATE_MS, 
            RESET_RATE_MS, 
            CHAT, 
            displayMessage
        );
        updaterTask.start();
    }
}
