package pl.undefine.cbb.ast;

import pl.undefine.cbb.utils.InternalException;

public class Operator extends Expression
{
    public OperatorType type;

    public Operator(OperatorType type)
    {
        this.type = type;
    }

    public int get_precedence() throws InternalException
    {
        return switch(type) {
            case Add, Subtract -> 95;
            case Multiply, Divide -> 90;
            default -> throw new InternalException("unknown operator type");
        };
    }
}
