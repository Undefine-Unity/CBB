package pl.undefine.cbb;

import pl.undefine.cbb.utils.LexerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer
{
    int file_id;
    byte[] file_content;
    int index;

    public Lexer(int file_id, byte[] file_content)
    {
        this.file_id = file_id;
        this.file_content = file_content;
    }

    List<Token> lex_file() throws LexerException
    {
        List<Token> tokens = new ArrayList<>();
        while (index < file_content.length)
        {
            skip_whitespace();

            if (file_content[index] == '(')
            {
                tokens.add(new Token(TokenType.LParen, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == ')')
            {
                tokens.add(new Token(TokenType.RParen, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '{')
            {
                tokens.add(new Token(TokenType.LCurly, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '}')
            {
                tokens.add(new Token(TokenType.RCurly, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == ';')
            {
                tokens.add(new Token(TokenType.Semicolon, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == ',')
            {
                tokens.add(new Token(TokenType.Comma, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '=')
            {
                tokens.add(new Token(TokenType.Equals, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '+')
            {
                tokens.add(new Token(TokenType.Plus, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '-')
            {
                tokens.add(new Token(TokenType.Minus, new Span(file_id, index, index + 1)));
                index++;
            }

            else if (file_content[index] == '*')
            {
                tokens.add(new Token(TokenType.Asterisk, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '/')
            {
                tokens.add(new Token(TokenType.ForwardSlash, new Span(file_id, index, index + 1)));
                index++;
            }
            else
            {
                tokens.add(lex_item());
            }
        }
        tokens.add(new Token(TokenType.Eof, new Span(file_id, index, index)));
        return tokens;
    }

    public static void dump_tokens(List<Token> tokens)
    {
        for (Token token : tokens)
        {
            System.out.printf("<Token type=\"%s\" value=\"%s\" span={file_id=\"%d\" start=\"%d\" end=\"%d\"}>\n", token.type, token.value != null ? token.value : "", token.span.file_id, token.span.start, token.span.end);
        }
    }

    void skip_whitespace()
    {
        while (file_content[index] == ' ' || file_content[index] == '\t' || file_content[index] == '\n' || file_content[index] == '\r')
            index++;
    }

    Token lex_item() throws LexerException
    {
        if (Character.isDigit(file_content[index]))
        {
            int start = index;
            while (index < file_content.length && Character.isDigit(file_content[index]))
            {
                index++;
            }

            byte[] number = Arrays.copyOfRange(file_content, start, index);

            return new Token(TokenType.Number, new Span(file_id, start, index), new String(number));
        }
        else if (file_content[index] == '"')
        {
            int start = index;
            index++;

            boolean escaped = false;
            while (index < file_content.length && (escaped || file_content[index] != '"'))
            {
                escaped = !escaped && file_content[index] == '\\';
                index++;
            }

            if (index == file_content.length || file_content[index] != '"')
            {
                throw new LexerException("expected quote", new Span(file_id, index, index));
            }

            byte[] string = Arrays.copyOfRange(file_content, start + 1, index);

            index++;
            return new Token(TokenType.String, new Span(file_id, start, index), new String(string));
        }
        else
        {
            int start = index;
            index++;

            while (index < file_content.length && (Character.isAlphabetic(file_content[index]) || Character.isDigit(file_content[index]) || file_content[index] == '_'))
            {
                index++;
            }

            if(start == index)
            {
                throw new LexerException("unknown character", new Span(file_id, start, index + 1));
            }

            byte[] name = Arrays.copyOfRange(file_content, start, index);

            return new Token(TokenType.Name, new Span(file_id, start, index), new String(name));
        }
    }
}
