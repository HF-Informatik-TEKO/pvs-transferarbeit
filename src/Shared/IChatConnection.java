package Shared;

import java.util.List;
import java.util.Date;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatConnection extends Remote {
    /**
     * Register as chat client with nickname.
     * Returns a client token to interact with the RPC.
     */
    public String register(String nickname, String password) throws RemoteException;

    /**
     * If the user is registere, store the message.
     * Else throw exception.
     */
    public String send(String userToken, String message) throws RemoteException;

    /**
     * If the user is registered, return the last messages since the date.
     * If no date is given (null) all messages are returned.
     * Else throw exception.
     */
    public List<ChatMessage> get(String userToken, Date dat) throws RemoteException;
}
