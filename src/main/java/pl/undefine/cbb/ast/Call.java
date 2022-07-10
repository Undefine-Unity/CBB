package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class Call extends Expression
{
    public String name;
    public List<Expression> params;

    public Call()
    {
        params = new ArrayList<>();
    }
}
