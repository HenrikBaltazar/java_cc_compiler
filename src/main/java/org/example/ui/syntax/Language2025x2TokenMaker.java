package org.example.ui.syntax;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.swing.text.Segment;
import java.util.List;

public class Language2025x2TokenMaker extends AbstractTokenMaker {

    private List<String> RESERVED_WORDS = List.of("begin","define","start","end","set","num","real","text","flag","read","show","if","then","else","true","false");

    @Override
    public Token getTokenList(Segment text, int startTokenType, int startOffset) {
        resetTokenList();

        int offset = startOffset;
        char[] array = text.array;
        int count = text.count;
        int end = text.offset + count;

        int currentTokenStart = text.offset;

        for (int i = text.offset; i < end; i++) {
            char c = array[i];

            if (Character.isLetter(c)) {
                int j = i;
                while (j < end && Character.isLetter(array[j])) j++;
                String word = new String(array, i, j - i);
                if (RESERVED_WORDS.contains(word)) {
                    addToken(text, i, j - 1, Token.RESERVED_WORD, offset + (i - text.offset));
                } else {
                    addToken(text, i, j - 1, Token.IDENTIFIER, offset + (i - text.offset));
                }
                i = j - 1;
                currentTokenStart = j;
            }
            else if (Character.isDigit(c)) {
                int j = i;
                while (j < end && Character.isDigit(array[j])) j++;
                addToken(text, i, j - 1, Token.LITERAL_NUMBER_DECIMAL_INT, offset + (i - text.offset));
                i = j - 1;
                currentTokenStart = j;
            }
            else if (c == '/' && i + 1 < end && array[i+1] == '/') {
                addToken(text, i, end - 1, Token.COMMENT_EOL, offset + (i - text.offset));
                // não retorne ainda, continue até addNullToken
                break;
            }
            else if (Character.isWhitespace(c)) {
                int j = i;
                while (j < end && Character.isWhitespace(array[j])) j++;
                addToken(text, i, j - 1, Token.WHITESPACE, offset + (i - text.offset));
                i = j - 1;
                currentTokenStart = j;
            }
            else {
                addToken(text, i, i, Token.NULL, offset + (i - text.offset));
                currentTokenStart = i + 1;
            }
        }

        addNullToken();
        return firstToken;
    }


    @Override
    public TokenMap getWordsToHighlight() {
        return null;
    }
}
