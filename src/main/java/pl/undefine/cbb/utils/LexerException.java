package pl.undefine.cbb.utils;

import pl.undefine.cbb.Span;

/**
 * Exception thrown only by the lexer
 */
public class LexerException extends RuntimeException
{
    public Span span;

    public LexerException(String message, Span span)
    {
        super(message);
        this.span = span;
    }
}
