package highlighting.core;

import java.awt.Color;

public record HighlightRegion(int start, int end, Color color) {
    @Override
    public String toString() {
        return "HighlightRegion[" + start + ", " + end + ")";
    }
}
