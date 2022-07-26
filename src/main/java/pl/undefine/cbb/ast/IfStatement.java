package pl.undefine.cbb.ast;

public class IfStatement extends Statement
{
    public Expression condition;
    public Block block;
    public Block else_block;
}
