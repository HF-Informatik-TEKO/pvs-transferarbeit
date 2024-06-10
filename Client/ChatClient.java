package Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Shared.IChatConnection;

public class ChatClient {
    public static void main(String[] args) {
        try {
            var chat = (IChatConnection)
                Naming.lookup("rmi://127.0.0.1:1200/Chat");
            
            NrgDrink(chat);
            ManaPool(chat);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void NrgDrink(IChatConnection chat) throws RemoteException {
        var registry = chat.register("NRG Drink");
        System.out.println("Registered user with token: " + registry);
        var sendt = chat.send(registry, "hello world");
        System.out.println(sendt);
        var got = chat.get(registry);
        System.out.println(got);
    }

    private static void ManaPool(IChatConnection chat) throws RemoteException {
        var registry = chat.register("Mana Pool");
        System.out.println("Registered user with token: " + registry);
        var sendt = chat.send(registry, "hex hex");
        System.out.println(sendt);
        var got = chat.get(registry);
        System.out.println(got);
    }
}
