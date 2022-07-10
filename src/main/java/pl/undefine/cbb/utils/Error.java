package pl.undefine.cbb.utils;

import pl.undefine.cbb.Span;

public class Error
{
    public String text;
    public Span span;

    public Error(String text, Span span)
    {
        this.text = text;
        this.span = span;
    }
}
