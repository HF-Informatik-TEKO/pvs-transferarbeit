package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatConnection extends Remote {
    /**
     * If the user is registere, store the message.
     * Else throw exception.
     */
    public String send(String userToken, String message) throws RemoteException;
    /**
     * If the user is registered, return the last X messages.
     * Else throw exception.
     */
    public String get(String userToken) throws RemoteException;
    /**
     * Register as chat client with nickname.
     * Returns a client token to interact with the RPC.
     */
    public String register(String nickname) throws RemoteException;
}
