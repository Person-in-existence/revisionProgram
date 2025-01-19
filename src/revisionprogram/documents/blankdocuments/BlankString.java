package revisionprogram.documents.blankdocuments;

import java.util.Objects;

public record BlankString(String string, Blank[] blanks) {
    public boolean isEqual(BlankString other) {
        // Compare the strings
        if (!Objects.equals(other.string(), string())) {
            return false;
        }

        // Compare the blanks
        Blank[] otherBlanks = other.blanks();
        Blank[] thisBlanks = blanks();

        if (otherBlanks.length != thisBlanks.length) {
            return false;
        }

        for (int index = 0; index < thisBlanks.length; index++) {
            Blank thisBlank = thisBlanks[index];
            Blank otherBlank = otherBlanks[index];
            if (!thisBlank.equals(otherBlank)) {
                return false;
            }
        }

        return true;

    }
}
