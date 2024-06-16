package Client.Gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import Client.ChatConnection;

import java.awt.event.*;

public class MessageSendJPanel extends JPanel {

    private final ChatWindow WINDOW;
    private final ChatConnection CHAT;
    private final KeyAdapter alChatSend;
    private final ActionListener alSendButton;

    private JTextField chatInput;
    private JButton sendButton;

    public MessageSendJPanel(ChatWindow window, ChatConnection chat) {
        WINDOW = window;
        this.CHAT = chat;

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(WINDOW.BORDER_THICKNESS, 0, 0, 0));
        this.setBackground(WINDOW.BACKGROUND_COLOR);

        chatInput = new JTextField();
        chatInput.setToolTipText("Write your oneline message here. (press ENTER to send)");
        this.add(chatInput, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        this.add(sendButton, BorderLayout.EAST);

        alSendButton = GetSendOnClickListener();
        alChatSend = GetSendOnEnterListener();

        sendButton.addActionListener(alSendButton);
        chatInput.addKeyListener(alChatSend);
    }

    public void sendMessage() {
        CHAT.sendMessage(chatInput.getText());
        chatInput.setText("");
    }

    @Override
    public void requestFocus() {
        chatInput.requestFocus();
    }

    @Override
    public void enable() {
        sendButton.setEnabled(true);
        chatInput.setEnabled(true);
    }

    @Override
    public void disable() {
        sendButton.setEnabled(false);
        chatInput.setEnabled(false);
    }
    
    private ActionListener GetSendOnClickListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
    }

    private KeyAdapter GetSendOnEnterListener() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        };
    }
}
