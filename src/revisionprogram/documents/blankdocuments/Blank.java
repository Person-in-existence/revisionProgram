package revisionprogram.documents.blankdocuments;

import java.util.Comparator;

public class Blank {
    private int start;
    private int end;
    public Blank(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public void shift(int amount) {
        this.start += amount;
        this.end += amount;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public void setStart(int start) {this.start = start;}
    public void setEnd(int end) {this.end = end;}
    public boolean doesIntersect(Blank otherBlank) {
        // There is an intersection if: the start point of the other is within this, or the start point of this is within the other, or the end of one is the same as the start of another
        int start = getStart();
        int end = getEnd();
        int otherStart = otherBlank.getStart();
        int otherEnd = otherBlank.getEnd();
        boolean otherThis = start <= otherStart & otherStart < end;
        boolean thisOther = otherStart <= start & start < otherEnd;
        boolean endCheck = start == otherEnd | end == otherStart;

        return otherThis | thisOther | endCheck;
    }
    public boolean equals(Blank otherBlank) {
        return getStart() == otherBlank.getStart() & getEnd() == otherBlank.getEnd();
    }
    public boolean contains(Blank otherBlank) {
        boolean start = getStart() < otherBlank.getStart() & otherBlank.getStart() < getEnd();
        boolean end = getStart() < otherBlank.getEnd() & otherBlank.getEnd() < getEnd();
        return start & end;
    }
    public boolean within(int index) {
        return getStart() <= index & index < getEnd();
    }
    public static Comparator<Blank> comparator = Comparator.comparingInt(Blank::getStart);
    public int length() {
        return getEnd() - getStart();
    }

}
