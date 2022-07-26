package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclaration extends Declaration
{
    public String name;
    public Type return_type;
    public List<VariableDeclaration> parameters;
    public Block block;

    public FunctionDeclaration()
    {
        parameters = new ArrayList<>();
    }
}
