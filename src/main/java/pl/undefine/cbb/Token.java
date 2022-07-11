package pl.undefine.cbb;

public class Token
{
    enum TokenType
    {
        Number,
        String,
        Name,
        LParen,
        RParen,
        LCurly,
        RCurly,
        Semicolon,
        Comma,
        Eof,
    };

    TokenType type;
    Span span;
    String value;

    public Token(TokenType type, Span span)
    {
        this.type = type;
        this.span = span;
    }

    public Token(TokenType type, Span span, String value)
    {
        this.type = type;
        this.span = span;
        this.value = value;
    }
}
