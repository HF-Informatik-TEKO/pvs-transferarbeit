package Shared;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;

public class ChatMessage implements Serializable {
    private final Date time = new Date();
    private final String user;
    private final String message;

    public ChatMessage(String user, String message) {
        this.user = user;
        this.message = message;
    }
    
    @Override
    public String toString() {
        var formatted = getTimeString();
        return String.format("%s | %s:\n-> %s", formatted, user, message);
    }
    
    public String toMessageHtml() {
        return toMessageHtml("");
    }

    public String toMessageHtml(String userName) {
        return String.format(""
            + "<div class='%s'>"
                + "<span class='sender'>%s | <strong>%s</strong>:</span><br>" // time and user
                + "<span class='message'>%s</span>" // message
            + "</div>",
            userName.equals(user) ? "self" : "",
            getTimeString(),
            user,
            message
        );
    }

    public String toErrorMessageHtml() {
        return String.format(""
            + "<div class=''>"
                + "<span class='sender'>%s | <strong>%s</strong>:</span><br>" // time and user
                + "<span class='error'>%s</span>" // message
            + "</div>",
            // userName.equals(user) ? "self" : "",
            getTimeString(),
            user,
            message
        );
    }

    public String getTimeString() {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var formatted = formatter.format(time);
        return formatted;
    }

    public Date getTime() {
        return time;
    }
}
