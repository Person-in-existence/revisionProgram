package revisionprogram.documents.blankdocuments;

import revisionprogram.Borders;
import revisionprogram.Main;
import revisionprogram.components.panellist.ListCard;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BlankViewCard extends ListCard {
    private JTextPane textPane;
    private BlankString blankString;
    private ArrayList<Adjustment> strikethroughs = new ArrayList<>();
    private static SimpleAttributeSet highlighted = new SimpleAttributeSet();
    private static SimpleAttributeSet notHighlighted = new SimpleAttributeSet();
    static {
        StyleConstants.setBackground(highlighted, Color.CYAN);
        StyleConstants.setBackground(notHighlighted, UIManager.getColor(""));
    }
    public BlankViewCard(BlankString blankString) {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        // Sort the blanks
        Arrays.sort(blankString.blanks(), Blank.comparator);
        this.blankString = blankString;

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(Main.factFont);
        textPane.setText(blankedString(blankString));
        this.add(textPane, constraints);

        this.setBorder(Borders.defaultBorder());
    }

    public void highlightBlank(Blank blank) {

        try {
            Highlighter highlighter = textPane.getHighlighter();
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(textPane.getSelectionColor());
            highlighter.removeAllHighlights();
            highlighter.addHighlight(blank.getStart(), blank.getEnd(), painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }
    public void revealBlank(Blank blank, boolean wasCorrect, String inputString) {
        // Make the strikethrough attributes - we will need them later
        SimpleAttributeSet strikethrough = new SimpleAttributeSet();
        StyleConstants.setStrikeThrough(strikethrough, true);

        // Add the input string
        StringBuilder stringBuilder = new StringBuilder(textPane.getText());
        for (int index = blank.getStart(); index < blank.getEnd(); index++) {
            int paneIndex = lookupIndex(index); // Look up the index because it might be messed up with incorrect items
            stringBuilder.setCharAt(paneIndex, blankString.string().charAt(index));
        }


        // If it wasnt correct, we want to insert the inputString before it
        if (!wasCorrect) {
            int startIndex = lookupIndex(blank.getStart());
            // We need to adjust the blanks for the textPane first - create an adjustment
            Adjustment adjustment = new Adjustment(blank.getStart(), inputString.length());
            strikethroughs.add(adjustment);
            // Add the characters to the stringBuilder in reverse
            for (int index = inputString.length() - 1; index > -1; index--) {
                stringBuilder.insert(startIndex, inputString.charAt(index));
            }
            // Strikethrough the incorrect answer
        }
        textPane.setText(stringBuilder.toString());

        // Reapply the strikethroughs using strikethroughs
        for (Adjustment adjustment: strikethroughs) {
            int adjustmentIndex = lookupIndex(adjustment.index());
            textPane.getStyledDocument().setCharacterAttributes(adjustmentIndex, adjustment.amount(), strikethrough, false);
        }
    }

    /**
     * Replaces the characters in the string which are blanked with _s, so you cant see them in the input
     * @param string The string to blank
     * @return The blanked string
     */
    private String blankedString(BlankString string) {
        // TODO: OPTIMISE TO BE MORE EFFICIENT - GO THROUGH BLANKS RATHER THAN STRING?
        StringBuilder stringBuilder = new StringBuilder(string.string().length());
        for (int charIndex = 0; charIndex < string.string().length(); charIndex++) {
            // Check if charIndex is in one of the blanks
            boolean isWithin = false;
            for (Blank blank: string.blanks()) {
                if (blank.within(charIndex)) {
                    // Blank it if it is
                    stringBuilder.append("_");
                    isWithin = true;
                    break;
                }
            }
            if (!isWithin) {
                stringBuilder.append(string.string().charAt(charIndex));
            }

        }
        return stringBuilder.toString();
    }
    protected BlankString getBlankString() {
        return blankString;
    }
    protected String getStringAt(Blank blank) {
        return blankString.getTextAt(blank);
    }

    private int lookupIndex(int index) {
        final int originalIndex = index;
        for (Adjustment adjustment: strikethroughs) {
            index += adjustment.adjustAmount(originalIndex);
        }
        return index;
    }

}
