package pl.undefine.cbb.ast;

public class VariableValue extends Expression
{
    public String variable_name;

    public VariableValue(String variable_name)
    {
        this.variable_name = variable_name;
    }
}
