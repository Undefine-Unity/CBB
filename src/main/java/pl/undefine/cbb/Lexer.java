package pl.undefine.cbb;

import pl.undefine.cbb.utils.Error;
import pl.undefine.cbb.utils.ErrorOr;

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

    ErrorOr<List<Token>> lex_file()
    {
        List<Token> tokens = new ArrayList<>();
        while (index < file_content.length)
        {
            skip_whitespace();

            if (file_content[index] == '(')
            {
                tokens.add(new Token(Token.TokenType.LParen, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == ')')
            {
                tokens.add(new Token(Token.TokenType.RParen, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '{')
            {
                tokens.add(new Token(Token.TokenType.LCurly, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == '}')
            {
                tokens.add(new Token(Token.TokenType.RCurly, new Span(file_id, index, index + 1)));
                index++;
            }
            else if (file_content[index] == ';')
            {
                tokens.add(new Token(Token.TokenType.Semicolon, new Span(file_id, index, index + 1)));
                index++;
            }
            else
            {
                tokens.add(lex_item().get_value());
            }
        }
        tokens.add(new Token(Token.TokenType.Eof, new Span(file_id, index, index)));
        return new ErrorOr<>(tokens);
    }

    void skip_whitespace()
    {
        while (file_content[index] == ' ' || file_content[index] == '\t' || file_content[index] == '\n' || file_content[index] == '\r')
            index++;
    }

    ErrorOr<Token> lex_item()
    {
        if (Character.isDigit(file_content[index]))
        {
            int start = index;
            while (index < file_content.length && Character.isDigit(file_content[index]))
            {
                index++;
            }

            byte[] number = Arrays.copyOfRange(file_content, start, index);

            return new ErrorOr<>(new Token(Token.TokenType.Number, new Span(file_id, start, index), new String(number)));
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
                return new ErrorOr<>(new Error("expected quote", new Span(file_id, index, index)));
            }

            byte[] string = Arrays.copyOfRange(file_content, start + 1, index);

            index++;
            return new ErrorOr<>(new Token(Token.TokenType.String, new Span(file_id, start, index), new String(string)));
        }
        else
        {
            int start = index;
            index++;

            while (index < file_content.length && (Character.isAlphabetic(file_content[index]) || Character.isDigit(file_content[index])))
            {
                index++;
            }

            byte[] name = Arrays.copyOfRange(file_content, start, index);

            return new ErrorOr<>(new Token(Token.TokenType.Name, new Span(file_id, start, index), new String(name)));
        }
    }
}
