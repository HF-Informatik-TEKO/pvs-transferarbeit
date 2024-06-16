package Client.Gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class MessageDisplayJPanel extends JEditorPane {
    
    private final HTMLEditorKit KIT;
    private final HTMLDocument DOC;
    private final String HTML_HEADER = ""
        + "<head><style>"
            + "body { font-size: 12px; font-family: Consolas, sans-serif; color: white; }"
            + ".sender { color: gray; font-style: italic; font-size: 11px}"
            + ".message { color: green; }"
            + ".self { text-align: right; }"
            + ".error { color: red; }"
        + "</style></head>";

    public MessageDisplayJPanel() {
        this.setBackground(Color.BLACK);
        this.setContentType("text/html");
        this.setEditable(false);
        KIT = (HTMLEditorKit) this.getEditorKit();
        DOC = new HTMLDocument();
        this.setDocument(DOC);

        tryAppendMessage(HTML_HEADER);
    }

    public boolean tryAppendMessage(String html) {
        try {
            KIT.insertHTML(DOC, DOC.getLength(), html, 0, 0, null);
            this.setCaretPosition(DOC.getLength());
            return true;
        } catch (Exception e) {
            // Ignore Exception.
            return false;
        }
    }

    public void resetDisplay() {
        this.setText("");
    }
}
