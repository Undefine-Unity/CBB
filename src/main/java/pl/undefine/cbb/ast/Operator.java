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
            case Multiply, Divide -> 100;
            case Add, Subtract -> 75;
            case Comparison, GreaterThan, LessThan, GreaterThanOrEqual, LessThanOrEqual -> 50;
            default -> throw new InternalException("unknown operator type");
        };
    }
}
