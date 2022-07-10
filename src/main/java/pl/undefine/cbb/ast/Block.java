package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class Block
{
    public List<Statement> statements;

    public Block()
    {
        statements = new ArrayList<>();
    }
}
