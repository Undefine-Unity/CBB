package pl.undefine.cbb;

/**
 * Possible types of a token
 *
 * @see Token
 */
public enum TokenType
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
    Equals,
    DoubleEquals,
    NotEquals,
    GreaterThan,
    LessThan,
    GreaterThanOrEqual,
    LessThanOrEqual,
    Plus,
    Minus,
    Asterisk,
    ForwardSlash,
    Eof,
}
