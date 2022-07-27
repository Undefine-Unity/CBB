package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class Call extends Expression
{
    public String name;
    public List<Expression> parameters;

    public Call()
    {
        parameters = new ArrayList<>();
    }
}
