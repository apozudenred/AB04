package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;

import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        List<HighlightRegion> all = new ArrayList<>();
        for (Token token : MiniJavaTokens.defaultTokens()) {
            all.addAll(token.test(text));
        }
        return all;
        // normalize() übernimmt Sortierung
    }

    @Override
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> normalized) {
        List<HighlightRegion> result = new ArrayList<>();
        for (HighlightRegion candidate : normalized) {
            boolean overlaps = false;
            for (HighlightRegion selected : result) {
                // Überlappung: [R.start, R.end) schneidet [S.start, S.end)
                if (candidate.start() < selected.end() && candidate.end() > selected.start()) {
                    overlaps = true;
                    break;
                }
            }
            if (!overlaps) {
                result.add(candidate);
            }
        }
        return result;
    }
}
