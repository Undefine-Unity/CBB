package pl.undefine.cbb;

import pl.undefine.cbb.ast.*;

public class Compiler
{
    public Compiler(ParsedFile parsed_file)
    {
        this.parsed_file = parsed_file;
    }

    String compile()
    {
        StringBuilder output = new StringBuilder();

        output.append("#include <stdio.h>\n");
        output.append("\n");

        for (Function function : parsed_file.functions)
        {
            output.append(compile_function(function));
            output.append("\n");
        }

        return output.toString();
    }

    String compile_function(Function function)
    {
        return function.return_type +
                " " +
                function.name +
                "()\n" +
                translate_block(function.block);
    }

    String translate_block(Block block)
    {
        StringBuilder output = new StringBuilder();

        output.append("{\n");

        for (Expression expression : block.expressions)
        {
            output.append("\t");
            output.append(translate_expression(expression));
            output.append(";\n");
        }

        output.append("}\n");

        return output.toString();
    }

    String translate_expression(Expression expression)
    {
        StringBuilder output = new StringBuilder();

        if (expression instanceof Call call)
        {
            if (call.name.equals("print"))
            {
                output.append("printf");
            }
            else
            {
                output.append(call.name);
            }

            output.append('(');

            for (Expression param : call.params)
            {
                output.append(translate_expression(param));
            }

            output.append(')');
        }
        else if(expression instanceof StringLiteral string)
        {
            output.append("\"");
            output.append(string.value);
            output.append("\"");
        }

        return output.toString();
    }

    ParsedFile parsed_file;
}
