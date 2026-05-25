package highlighting.regex;

import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScanningHighlighterTest {

    private ScanningHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new ScanningHighlighter();
    }

    // ── Basic sanity ──────────────────────────────────────────────────────────

    @Test
    void emptyText() {
        var regions = highlighter.computeRegions("");
        assertTrue(regions.isEmpty());
    }

    @Test
    void noMatchText() {
        var regions = highlighter.computeRegions("   \t  ");
        assertTrue(regions.isEmpty());
    }

    @Test
    void singleKeyword() {
        var regions = highlighter.computeRegions("public");
        assertEquals(1, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(6, regions.get(0).end());
    }

    // ── No overlaps ───────────────────────────────────────────────────────────

    @Test
    void resultIsNonOverlapping() {
        var regions = highlighter.computeRegions("public class Foo { return null; }");
        for (int i = 0; i < regions.size() - 1; i++) {
            assertTrue(regions.get(i).end() <= regions.get(i + 1).start(),
                "Regions must not overlap: " + regions.get(i) + " and " + regions.get(i + 1));
        }
    }

    @Test
    void resultIsSorted() {
        var regions = highlighter.computeRegions("public class Foo { return null; }");
        for (int i = 0; i < regions.size() - 1; i++) {
            assertTrue(regions.get(i).start() <= regions.get(i + 1).start(),
                "Regions must be sorted by start");
        }
    }

    // ── Longest match wins ────────────────────────────────────────────────────

    @Test
    void longestMatchWins_javadocVsBlock() {
        // /** ... */ should be matched as Javadoc (longer/earlier token), not block comment
        String text = "/** javadoc */";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        // Javadoc color
        assertEquals(regions.get(0).color(), highlighting.presets.MiniJavaColours.JAVADOC);
    }

    @Test
    void longestMatchWins_keywordVsIdentifier() {
        // "publicKey" should NOT match keyword "public" because it's not a word boundary
        var regions = highlighter.computeRegions("publicKey");
        assertTrue(regions.isEmpty() || regions.stream().noneMatch(
            r -> r.color().equals(highlighting.presets.MiniJavaColours.KEYWORD)));
    }

    // ── Scanner skips non-matching characters ─────────────────────────────────

    @Test
    void skipsNonMatchingChars() {
        // Whitespace and symbols between keywords should not block scanning
        var regions = highlighter.computeRegions("  public  ");
        assertEquals(1, regions.size());
        assertEquals(2, regions.get(0).start());
        assertEquals(8, regions.get(0).end());
    }

    @Test
    void multipleKeywordsWithGaps() {
        var regions = highlighter.computeRegions("public class Foo");
        assertEquals(2, regions.size());
    }

    // ── Keyword inside comment is NOT highlighted ──────────────────────────────

    @Test
    void keywordInsideLineComment_notHighlightedSeparately() {
        // Scanner hits // first → consumes whole line comment → keyword inside not re-matched
        String text = "// public return";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(0).end());
    }

    @Test
    void keywordInsideBlockComment_notHighlighted() {
        String text = "/* public */";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(highlighting.presets.MiniJavaColours.COMMENT, regions.get(0).color());
    }

    @Test
    void keywordInsideString_notHighlighted() {
        String text = "\"public\"";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(highlighting.presets.MiniJavaColours.STRING, regions.get(0).color());
    }

    // ── Tie-breaking: earlier token in list wins ───────────────────────────────

    @Test
    void tieBreaking_javadocBeforeBlockComment() {
        // Both Javadoc and Block patterns could match "/**...*/" 
        // Javadoc token is first in list → wins
        String text = "/** x */";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(highlighting.presets.MiniJavaColours.JAVADOC, regions.get(0).color());
    }

    // ── Combination tests ─────────────────────────────────────────────────────

    @Test
    void combinationMatchAndNonMatch() {
        // Text with keywords and non-matchable symbols
        String text = "x=public;";
        var regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(2, regions.get(0).start());
        assertEquals(8, regions.get(0).end());
    }

    @Test
    void fullCodeSnippet() {
        String text = "public class Foo { // comment\n  return null;\n}";
        var regions = highlighter.computeRegions(text);
        // Must be sorted and non-overlapping
        for (int i = 0; i < regions.size() - 1; i++) {
            assertTrue(regions.get(i).end() <= regions.get(i + 1).start());
        }
        // Must contain at least the keywords and the comment
        assertTrue(regions.size() >= 3);
    }
}
