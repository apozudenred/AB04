package highlighting.core;

import java.util.List;

public abstract class SyntaxHighlighter {

    public final List<HighlightRegion> computeRegions(String text) {
        var candidates = collectMatches(text);
        var normalized = normalize(candidates);
        var resolved = resolveConflicts(normalized);
        return resolved;
    }

    public abstract List<HighlightRegion> collectMatches(String text);

    public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
        return candidates.stream()
                .filter(r -> r.start() < r.end())
                .sorted(
                        (a, b) -> {
                            int c = Integer.compare(a.start(), b.start());
                            return (c != 0) ? c : Integer.compare(b.end(), a.end());
                        })
                .toList();
    }

    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> normalized) {
        return normalized;
    }
}
