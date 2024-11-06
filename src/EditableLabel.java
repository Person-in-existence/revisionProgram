import com.formdev.flatlaf.ui.FlatBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditableLabel extends JTextField {
    public EditableLabel(String text, JPanel toRevalidate) {
        super(text);
        this.setEditable(false);
        this.setFocusable(false);
        this.setBorder(new EmptyBorder(1,1,1,1));
        this.setFont(Main.textContentFont);
        // Add action listener (listen for double clicks)
        JTextField reference = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Check for double click
                    reference.setEditable(true);
                    reference.setFocusable(true);
                    reference.requestFocus();
                    reference.setBorder(new FlatBorder());
                    reference.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            reference.setEditable(false);
                            reference.setFocusable(false);
                            reference.setBorder(new EmptyBorder(1,1,1,1));

                            // Make sure the size is correct
                            FontMetrics metrics = reference.getFontMetrics(reference.getFont());
                            int width = metrics.stringWidth(reference.getText()) + 10;
                            reference.setPreferredSize(new Dimension(width, reference.getPreferredSize().height));
                            reference.setMinimumSize(new Dimension(width, reference.getPreferredSize().height));
                            toRevalidate.revalidate();
                        }
                    });
                }
            }
        });
    }
}
