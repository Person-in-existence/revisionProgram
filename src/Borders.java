import com.formdev.flatlaf.ui.FlatBorder;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Borders {
    public static Border defaultBorder() {
        return new CompoundBorder(new FlatBorder(), new EmptyBorder(1,1,1,1));
    }
    public static Border highlightedBorder() {
        FlatBorder border = new FlatBorder();
        border.applyStyleProperty("borderColor", Color.cyan);
        border.applyStyleProperty("focusWidth", 1);
        return border;
    }
}
