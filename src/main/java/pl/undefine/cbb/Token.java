package pl.undefine.cbb;

public class Token
{
    public TokenType type;
    public Span span;
    public String value;

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
