package pl.undefine.cbb.utils;

import pl.undefine.cbb.Span;

/**
 * Exception thrown only by the parser
 */
public class ParserException extends RuntimeException
{
    public Span span;

    public ParserException(String message, Span span)
    {
        super(message);
        this.span = span;
    }
}
