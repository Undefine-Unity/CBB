package pl.undefine.cbb;

import pl.undefine.cbb.ast.*;
import pl.undefine.cbb.ast.Number;

public class Compiler
{
    public Compiler(ParsedFile parsed_file)
    {
        this.parsed_file = parsed_file;
    }

    String compile()
    {
        StringBuilder output = new StringBuilder();

        output.append("#include <runtime/lib.h>\n");
        output.append("\n");

        for (Declaration declaration : parsed_file.declarations)
        {
            output.append(compile_declaration(declaration));
            output.append("\n");
        }

        return output.toString();
    }

    String compile_declaration(Declaration declaration)
    {
        if (declaration instanceof Function function)
            return compile_function(function);
        else if (declaration instanceof Variable variable)
            return compile_variable(variable);
        else
        {
            if(Main.is_debug())
                assert false;
            else
                System.out.println("Internal error");
            System.exit(2);
            return "";
        }
    }

    String compile_function(Function function)
    {
        return function.return_type.cpp_name +
                " " +
                function.name +
                "()\n" +
                compile_block(function.block);
    }

    String compile_variable(Variable variable)
    {
        return variable.type.cpp_name +
                " " +
                variable.name +
                (variable.asignment != null ? " = " + compile_expression(variable.asignment) : "") +
                ";\n";
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
        if(statement instanceof Declaration declaration)
            return compile_declaration(declaration);
        else if(statement instanceof Expression expression)
            return compile_expression(expression);
        else
        {
            if(Main.is_debug())
                assert false;
            else
                System.out.println("Internal error");
            System.exit(2);
            return "";
        }
    }

    String compile_expression(Expression expression)
    {
        StringBuilder output = new StringBuilder();

        if (expression instanceof Call call)
        {
            if (call.name.equals("print"))
            {
                output.append("std::cout");

                for (Expression param : call.params)
                {
                    output.append(" << ");
                    output.append(compile_expression(param));
                }
            }
            else
            {
                output.append(call.name);
                output.append('(');

                for (Expression param : call.params)
                {
                    output.append(compile_expression(param));
                }

                output.append(')');
            }
        }
        else if (expression instanceof StringLiteral string)
        {
            output.append("\"");
            output.append(string.value);
            output.append("\"");
        }
        else if (expression instanceof Number number)
        {
            output.append(number.number);
        }
        else if (expression instanceof VariableValue variable_value)
        {
            output.append(variable_value.variable_name);
        }
        else if (expression instanceof BinaryOperation binary_operation)
        {
            output.append(compile_expression(binary_operation.left_side));
            output.append(compile_expression(binary_operation.operator));
            output.append(compile_expression(binary_operation.right_side));
        }
        else if (expression instanceof Operator operator)
        {
            output.append(switch(operator.type) {
                case Add -> "+";
                case Subtract -> "-";
                case Multiply -> "*";
                case Divide -> "/";
            });
        }

        return output.toString();
    }

    ParsedFile parsed_file;
}
