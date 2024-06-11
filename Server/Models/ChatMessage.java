package Server.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable; 

public class ChatMessage implements Serializable {
    public Date time;
    public String user;
    public String message;

    @Override
    public String toString() {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var formatted = formatter.format(time);
        return formatted + " | " + user + " | " + message;
    }
}
