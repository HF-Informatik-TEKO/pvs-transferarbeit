package Client;

import Client.Gui.MessageDisplayJPanel;
import java.util.Date;

public class GuiUpdater extends Thread {
    
    public boolean isRunning = true;
    private int refreshTime;
    private long resetTime;
    private ChatConnection connection;
    private MessageDisplayJPanel gui;

    public GuiUpdater(
        int refreshTime,
        long resetTime,
        ChatConnection connection,
        MessageDisplayJPanel gui
        ) 
    {
        this.refreshTime = refreshTime;
        this.resetTime = resetTime;
        this.connection = connection;
        this.gui = gui;
    }

    @Override
    public void run() {
        var lastReset = System.currentTimeMillis();
        Date lastRecieved = null;

        while (isRunning) {
            var startRunTime = System.currentTimeMillis();
            try {
                if ((System.currentTimeMillis() - lastReset) > resetTime) {
                    lastRecieved = null;
                    gui.resetDisplay();
                    lastReset = System.currentTimeMillis();
                }

                var fetchedMessages = connection.getMessages(lastRecieved);
                if (fetchedMessages.size() == 0) {
                    continue;
                }

                var sb = new StringBuilder();
                for (var m : fetchedMessages) {
                    sb.append(m.toHtml(connection.getUserName()));
                }

                var isDisplayed = gui.tryAppendMessage(sb.toString());
                if (isDisplayed) {
                    lastRecieved = new Date();
                }
            } finally {
                try {
                    var passedTime = System.currentTimeMillis() - startRunTime;
                    Thread.sleep(Math.max(0, refreshTime - passedTime));
                } catch (InterruptedException e1) {
                    // Ignore interrupt.
                }
            }
        }
    }
}
