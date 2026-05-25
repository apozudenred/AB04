package highlighting.regex;

import highlighting.core.HighlightRegion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private final Pattern pattern;
    private final Color color;
    private final int matchingGroup;

    public Token(Pattern pattern, Color color) {
        this(pattern, color, 0);
    }

    public Token(Pattern pattern, Color color, int matchingGroup) {
        this.pattern = pattern;
        this.color = color;
        this.matchingGroup = matchingGroup;
    }

    public List<HighlightRegion> test(String s) {
        List<HighlightRegion> regions = new ArrayList<>();
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            int start = matcher.start(matchingGroup);
            int end = matcher.end(matchingGroup);
            regions.add(new HighlightRegion(start, end, color));
        }
        return regions;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Color getColor() {
        return color;
    }

    public int getMatchingGroup() {
        return matchingGroup;
    }
}
