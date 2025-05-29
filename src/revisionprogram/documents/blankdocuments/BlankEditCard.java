package revisionprogram.documents.blankdocuments;

import revisionprogram.Borders;
import revisionprogram.Main;
import revisionprogram.components.panellist.ListCard;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BlankEditCard extends ListCard {
    private JTextPane textPane;
    private Dimension size;
    private int textAreaWidth;
    private boolean doListener;
    private ArrayList<Blank> blanks = new ArrayList<>();
    public static final SimpleAttributeSet notBlankAttributes = new SimpleAttributeSet();
    public static final SimpleAttributeSet blankAttributes = new SimpleAttributeSet();
    static {
        // Underline blank
        StyleConstants.setUnderline(blankAttributes, true);
        StyleConstants.setUnderline(notBlankAttributes, false);
    }
    public BlankEditCard(int width) {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;

        size = new Dimension(width, (int) (width*1.618/6));
        textAreaWidth = (int) (size.width*0.5);

        textPane = new JTextPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(textAreaWidth, super.getPreferredSize().height);
            }
        };
        this.add(textPane, constraints);

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setUnderline(attributeSet, true);

        // Add a key listener to the textPane, to get rid of enters (otherwise they mess up the blanks)
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check whether the key pressed was enter
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume(); // Stop the key from being typed
                }
            }
        });
        textPane.getStyledDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextInsert(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextRemove(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        // Blank selection button
        constraints.gridx++;
        JButton blankSelection = new JButton(Main.strings.getString("blankSelectionButton"));
        blankSelection.setFocusable(false);
        blankSelection.addActionListener(e->blankSelection());
        this.add(blankSelection, constraints);

        // Delete button
        constraints.gridx++;
        JButton deleteButton = new JButton(Main.strings.getString("factDelete"));
        deleteButton.addActionListener(e->{
            delete();
        });

        this.add(deleteButton, constraints);

        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0), Borders.defaultBorder())); // Use a compound border to easily add padding


    }
    public Blank[] getBlanks() {
        return blanks.toArray(new Blank[0]);
    }

    public void setBlanks(Blank[] blanks) {
        this.blanks = new ArrayList<>();
        // Copy so we don't change the original
        for (Blank blank : blanks) {
            this.blanks.add(new Blank(blank));
        }
        renderBlanks();
    }

    public String getText() {
        return textPane.getText();
    }
    protected void setText(String text) {
        textPane.setText(text);
    }
    public BlankString getBlankString() {
        return new BlankString(getText(), getBlanks());
    }

    @Override
    public void onResize(int width) {
        textAreaWidth = (int) (width*0.5);
    }

    private void handleIntersections(Blank blank) {
        // Get all the blanks which intersect this
        ArrayList<Blank> intersections = new ArrayList<>();

        for (Blank otherBlank : blanks) {
            if (otherBlank.doesIntersect(blank) & otherBlank != blank/*check that this isn't the same object*/) {
                intersections.add(otherBlank);
            }
        }

        // TODO: TEXT BASED GUESSES ON WHAT THE USER WANTS TO DO (SPACE POSITIONS ETC)
        // Handle having only one first
        if (intersections.size() == 1) {
            Blank intersection = intersections.get(0);

            // If the intersection is identical to the current blank, remove it and return
            if (intersection.equals(blank)) {
                blanks.remove(intersection);
                return;
            }

            // If the start|end are equal to each other, and the other one is within the intersection, remove blank from the intersection
            if (blank.getStart() == intersection.getStart() & blank.getEnd() < intersection.getEnd()) {
                intersection.setStart(blank.getEnd());
                return;
            }
            if (blank.getEnd() == intersection.getEnd() & blank.getStart() > intersection.getStart()) {
                intersection.setEnd(blank.getStart());
                return;
            }

            // If the blank is contained within the intersection, split it into two
            if (intersection.contains(blank)) {
                Blank blank1 = new Blank(intersection.getStart(), blank.getStart());
                Blank blank2 = new Blank(blank.getEnd(), intersection.getEnd());
                blanks.remove(intersection);
                blanks.add(blank1);
                blanks.add(blank2);
                return;
            }

            // If not, make intersection change to include current blank
            int start = blank.getStart();
            if (start < intersection.getStart()) {
                intersection.setStart(start);
            }
            int end = blank.getEnd();
            if (end > intersection.getEnd()) {
                intersection.setEnd(end);
            }

            return;
        }

        // If there are multiple, remove the individual ones and combine everything into one
        int earliestStart = blank.getStart();
        int latestEnd = blank.getEnd();
        for (Blank intersection : intersections) {
            if (intersection.getStart() < earliestStart) {
                earliestStart = intersection.getStart();
            }
            if (intersection.getEnd() > latestEnd) {
                latestEnd = intersection.getEnd();
            }
            blanks.remove(intersection);
        }
        blanks.add(new Blank(earliestStart,latestEnd));
    }

    private void blankSelection() {
        int selectionStart = textPane.getSelectionStart();
        int selectionEnd = textPane.getSelectionEnd();
        // Make a blank from the selection
        Blank blank = new Blank(selectionStart, selectionEnd);

        // Check for any other blanks which intersect with this one
        boolean isIntersection = false;

        for (Blank otherBlank: blanks) {
            if (blank.doesIntersect(otherBlank)) {
                isIntersection = true;
                break;
            }
        }

        // If there is an intersection, handle it
        if (isIntersection) {
            handleIntersections(blank);
        } else {
            // Otherwise, just add the blank to blanks
            blanks.add(blank);
        }
        // Render the blanks so it shows up
        renderBlanks();
    }

    private void renderBlanks() {
        SwingUtilities.invokeLater(()->{
            // First, set the whole JTextPane to be not blank (to get rid of any potential old blanks)
            int textLength = textPane.getText().length();
            textPane.getStyledDocument().setCharacterAttributes(0, textLength, notBlankAttributes, false);

            // Highlight each individual blank
            for (Blank blank: blanks) {
                int offset = blank.getStart();
                int length = blank.getEnd()-offset;
                textPane.getStyledDocument().setCharacterAttributes(offset, length, blankAttributes, false);
            }
        });
    }

    private void onTextInsert(DocumentEvent e) {
        // e.type should be insert

        // If e is at the end of the text box, then we dont need to care - it cant affect anything
        int offset = e.getOffset();
        int amount = e.getLength(); // Account for long inserts - copy paste etc
        int textLength = textPane.getText().length();
        if (offset == textLength) {
            // End here
            renderBlanks();
            return;
        }

        // Otherwise, we need to adjust the text
        for (Blank blank: blanks) {
            // Check if the blank needs adjusting: if its start is after or at the offset, we can shift it
            int start = blank.getStart();
            int end = blank.getEnd();
            if (start >= offset) {
                // shift the blank by the number of characters inserted
                blank.shift(amount);
            } else if (end >= offset) {
                // Now check if just the end is after
                blank.setEnd(end + amount);
            }
        }

        // Now render the blanks
        renderBlanks();
    }

    private void onTextRemove(DocumentEvent e) {
        int offset = e.getOffset();
        int amount = e.getLength();

        for (Blank blank: blanks) {
            // Check if it needs adjusting: if its start is after or at the offset, we can shift
            int start = blank.getStart();
            int end = blank.getEnd();
            if (end - start <= amount && start >= offset && start <= offset + amount) { // Check whether the blank needs to be removed
                SwingUtilities.invokeLater(()->blanks.remove(blank));
                continue;
            }
            if (start > offset) {
                // Shift the blank by the negative amount
                blank.shift(-amount);
            } else if (end > offset) { // Just greater than: otherwise it has an issue if you delete the text after it
                blank.setEnd(end - amount);
            }
        }
        // Render the blanks
        renderBlanks();
    }
}
