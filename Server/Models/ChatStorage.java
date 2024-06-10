package Server.Models;

import java.util.ArrayList;

import Server.MySemaphore;

public class ChatStorage<T> {

    private MySemaphore s = new MySemaphore(10);
    private ArrayList<T> list = new ArrayList<>();

    public ChatStorage() {
    }

    public void add(T obj) {
        list.add(obj);
    }
}
