package revisionprogram.documents.blankdocuments;

public record Adjustment(int index, int amount) {
    public int adjustAmount(int index) {
        if (index > this.index) {
            return amount();
        }
        else {
            return 0; // No adjustment
        }
    }
}
