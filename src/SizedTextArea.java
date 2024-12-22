import javax.swing.*;
import java.awt.*;

public class SizedTextArea extends JTextArea {
    private int width;
    public SizedTextArea(int width) {
        super();
        this.width = width;
        // Set to wrap at the end of words
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
    }
    public SizedTextArea(String text, int width) {
        super(text);
        this.width = width;
        // Set to wrap at the end of words
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
    }
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = width;
        return size;
    }
    public void setWidth(int width) {
        this.width = width;
    }
}
