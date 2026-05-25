package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ScanningHighlighter extends SyntaxHighlighter {

    /**
     * Liest den Text zeichenweise von links nach rechts.
     * An jeder Position wird das längste passende Token gewählt.
     * Bei Gleichstand gewinnt das Token, das früher in MiniJavaTokens steht.
     * Kein Token passt → Index um 1 erhöhen (i++).
     * Token passt → Index direkt hinter das Match setzen (i = end).
     *
     * Die erzeugte Liste ist bereits sortiert, überlappungsfrei und gültig.
     */
    @Override
    public List<HighlightRegion> collectMatches(String text) {
        List<Token> tokens = MiniJavaTokens.defaultTokens();
        List<HighlightRegion> result = new ArrayList<>();

        int i = 0;
        while (i < text.length()) {
            HighlightRegion best = null;
            int bestLength = 0;

            for (Token token : tokens) {
                Matcher matcher = token.getPattern().matcher(text);
                // Suche Match, das genau an Position i beginnt
                if (matcher.find(i)) {
                    int group = token.getMatchingGroup();
                    int start = matcher.start(group);
                    int end = matcher.end(group);

                    if (start == i) {
                        int length = end - start;
                        if (length > bestLength) {
                            bestLength = length;
                            best = new HighlightRegion(start, end, token.getColor());
                        }
                        // Bei Gleichstand: erstes Token gewinnt (List.of-Reihenfolge)
                    }
                }
            }

            if (best != null) {
                result.add(best);
                i = best.end();
            } else {
                i++;
            }
        }

        return result;
    }

    /**
     * collectMatches liefert bereits eine normalisierte Liste →
     * normalize gibt sie unverändert zurück (Identität).
     */
    @Override
    public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
        return candidates;
    }
}
