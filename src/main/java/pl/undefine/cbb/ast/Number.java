package pl.undefine.cbb.ast;

public class Number extends Expression
{
    public long number;

    public Number(long number)
    {
        this.number = number;
    }
}
