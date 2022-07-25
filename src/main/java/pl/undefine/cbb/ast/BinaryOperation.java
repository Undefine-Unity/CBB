package pl.undefine.cbb.ast;

public class BinaryOperation extends Expression
{
    public Expression left_side;
    public Expression operator;
    public Expression right_side;

    public BinaryOperation(Expression left_side, Expression operator, Expression right_side)
    {
        this.left_side = left_side;
        this.operator = operator;
        this.right_side = right_side;
    }
}
