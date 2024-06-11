package Shared;

import Server.Models.*;
import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatConnection extends Remote {
    /**
     * If the user is registere, store the message.
     * Else throw exception.
     */
    public String send(String userToken, String message) throws RemoteException;

    /**
     * Register as chat client with nickname.
     * Returns a client token to interact with the RPC.
     */
    public String register(String nickname) throws RemoteException;
    
    /**
     * If the user is registered, return the last messages since the date.
     * If no date is given (null) all messages are returned.
     * Else throw exception.
     */
    public List<ChatMessage> get(String userToken, Date dat) throws RemoteException;
}
