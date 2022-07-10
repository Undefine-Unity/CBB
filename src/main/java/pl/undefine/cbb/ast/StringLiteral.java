package pl.undefine.cbb.ast;

public class StringLiteral extends Expression
{
    public String value;

    public StringLiteral(String value)
    {
        this.value = value;
    }
}
