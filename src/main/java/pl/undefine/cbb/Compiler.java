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

        output.append("#include \"runtime/lib.h\"\n");
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
                compile_block(function.block);
    }

    String compile_block(Block block)
    {
        StringBuilder output = new StringBuilder();

        output.append("{\n");

        for (Statement statement : block.statements)
        {
            output.append("\t");
            output.append(compile_statement(statement));
            output.append(";\n");
        }

        output.append("}\n");

        return output.toString();
    }

    String compile_statement(Statement statement)
    {
        return compile_expression((Expression) statement);
    }

    String compile_expression(Expression expression)
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
                output.append(compile_expression(param));
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
