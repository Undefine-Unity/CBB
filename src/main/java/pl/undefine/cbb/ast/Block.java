package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class Block
{
    public List<Expression> expressions;

    public Block()
    {
        expressions = new ArrayList<>();
    }
}
