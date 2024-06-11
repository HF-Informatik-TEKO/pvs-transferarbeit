package Client;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import Server.Models.*;
import java.util.*;

import Shared.IChatConnection;

public class ChatClient {

    private static String userToken;
    private static Date lastRecieved;

    public static void main(String[] args) {

        try {
            var chat = (IChatConnection) Naming.lookup("rmi://127.0.0.1:1200/Chat");
            // var res = chat.get();
            startWindow(chat);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }


    private static void startWindow(IChatConnection chat) {

        var window = new JFrame("My Window");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(500, 500);
        
        var panel = new JPanel();
        panel.setBackground(new Color(100, 100, 100));
        
        var registrationInput = new JTextField(21);
        panel.add(registrationInput);
        
        var registerButton = new JButton("Register");
        panel.add(registerButton);
        
        var textArea = new JTextArea(10, 30);
        // var textArea = new JLabel();
        var scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(window.getWidth() - 30, window.getHeight() - 200));
        textArea.setEnabled(false);
        panel.add(scrollPane);
        
        var chatInput = new JTextField(24);
        chatInput.setEnabled(false);
        chatInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("send message");
                    try {
                        chat.send(userToken, chatInput.getText());
                        chatInput.setText("");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        panel.add(chatInput);
        
        var sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        panel.add(sendButton);

        registerButton.addActionListener(e -> {
            System.out.println("registry");
            try {
                userToken = chat.register(registrationInput.getText());
                System.out.println("Usertoken: " + userToken);
                chatInput.setEnabled(true);
                sendButton.setEnabled(true);
                registerButton.setEnabled(false);
                registrationInput.setEditable(false);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });
        sendButton.addActionListener(e -> {
            System.out.println("send message");
            try {
                chat.send(userToken, chatInput.getText());
                chatInput.setText("");
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });

        window.add(panel);
        window.setVisible(true);

        new Thread(() -> fetchMessages(chat, textArea, scrollPane)).start();
    }

    private static void fetchMessages(IChatConnection chat, JTextArea textArea, JScrollPane scroll) {
        while (true) {
            if (userToken != null) {
                try {
                    var delta = chat.get(userToken, lastRecieved);
                    lastRecieved = new Date();
                    if (delta.size() > 0) {
                        for (var m : delta) {
                            textArea.append(m.toString() + "\n");
                        }
                        scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
                    }
                    // var res = chat.get(userToken);
                    // if (recieved != res) {
                    //     recieved = res;
                        // textArea.setText("");
                        // for (var c : res) {
                        //     textArea.append(c.time + " | " + c.user + " | " + c.message);
                        // }
                    //     textArea.setText(res);
                    //     scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
                    // }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
