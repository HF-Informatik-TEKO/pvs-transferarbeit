package Shared;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;

public class ChatMessage implements Serializable {
    private final MessageType type;
    private final Date time = new Date();
    private final String user;
    private final String message;

    public ChatMessage(String user, String message, MessageType type) {
        this.user = user;
        this.message = message;
        this.type = type;
    }
    
    @Override
    public String toString() {
        var formatted = getTimeString();
        return String.format("%s | %s:\n-> %s", formatted, user, message);
    }

    public String toHtml() {
        if (type == MessageType.Error) {
            return toErrorMessageHtml();
        }
        return toMessageHtml("");
    }

    public String toHtml(String userName) {
        if (type == MessageType.Error) {
            return toErrorMessageHtml();
        }
        return toMessageHtml(userName);
    }

    private String toMessageHtml(String userName) {
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

    private String toErrorMessageHtml() {
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
