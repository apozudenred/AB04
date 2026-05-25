package highlighting.presets;

import highlighting.regex.Token;

import java.util.List;
import java.util.regex.Pattern;

public class MiniJavaTokens {

    public static List<Token> defaultTokens() {
        return List.of(
            // Javadoc-Kommentare ZUERST (vor normalem Block-Kommentar!)
            new Token(
                Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL),
                MiniJavaColours.JAVADOC
            ),
            // Mehrzeilige Kommentare /* ... */
            new Token(
                Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL),
                MiniJavaColours.COMMENT
            ),
            // Einzeilige Kommentare // bis Zeilenende
            new Token(
                Pattern.compile("//[^\n]*"),
                MiniJavaColours.COMMENT
            ),
            // Strings: alles zwischen " und dem nächsten "
            new Token(
                Pattern.compile("\"[^\"]*\""),
                MiniJavaColours.STRING
            ),
            // Characters: genau ein Zeichen zwischen ' und '
            new Token(
                Pattern.compile("'.'"),
                MiniJavaColours.CHARACTER
            ),
            // Keywords (als ganze Wörter)
            new Token(
                Pattern.compile(
                    "\\b(package|import|class|public|private|protected|final|return|null|new|" +
                    "if|else|for|while|do|switch|case|break|continue|void|static|abstract|" +
                    "interface|extends|implements|throws|throw|try|catch|finally|this|super|" +
                    "true|false|instanceof)\\b"
                ),
                MiniJavaColours.KEYWORD
            ),
            // Annotationen: @Bezeichner
            new Token(
                Pattern.compile("@[A-Za-z][A-Za-z0-9-]*"),
                MiniJavaColours.ANNOTATION
            ),
            // Zahlen
            new Token(
                Pattern.compile("\\b\\d+[Ll]?\\b"),
                MiniJavaColours.NUMBER
            )
        );
    }
}
