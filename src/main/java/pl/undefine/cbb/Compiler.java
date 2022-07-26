package pl.undefine.cbb;

import pl.undefine.cbb.ast.Number;
import pl.undefine.cbb.ast.*;
import pl.undefine.cbb.utils.InternalException;

public class Compiler
{
    ParsedFile parsed_file;

    public Compiler(ParsedFile parsed_file)
    {
        this.parsed_file = parsed_file;
    }

    String compile() throws InternalException
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

    String compile_declaration(Declaration declaration) throws InternalException
    {
        if (declaration instanceof FunctionDeclaration function_declaration)
            return compile_function(function_declaration);
        else if (declaration instanceof VariableDeclaration variable_declaration)
            return compile_variable(variable_declaration) + ";\n";
        else
        {
            throw new InternalException("Unknown type of a declaration");
        }
    }

    String compile_function(FunctionDeclaration function_declaration) throws InternalException
    {
        StringBuilder output = new StringBuilder();
        output.append(function_declaration.return_type.cpp_name);
        output.append(" ");
        output.append(function_declaration.name);
        output.append("(");
        for (VariableDeclaration parameter : function_declaration.parameters)
        {
            output.append(compile_variable(parameter));
        }
        output.append(")\n");
        output.append(compile_block(function_declaration.block));
        return output.toString();
    }

    String compile_variable(VariableDeclaration variable_declaration) throws InternalException
    {
        return variable_declaration.type.cpp_name +
                " " +
                variable_declaration.name +
                (variable_declaration.asignment != null ? " = " + compile_expression(variable_declaration.asignment) : "");
    }

    String compile_block(Block block) throws InternalException
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

    String compile_statement(Statement statement) throws InternalException
    {
        if (statement instanceof Declaration declaration)
            return compile_declaration(declaration);
        else if (statement instanceof Expression expression)
            return compile_expression(expression);
        else if (statement instanceof IfStatement if_statement)
            return compile_if_statement(if_statement);
        else
        {
            throw new InternalException("Unknown statement");
        }
    }

    String compile_expression(Expression expression) throws InternalException
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
            output.append(switch (operator.type)
                    {
                        case Add -> "+";
                        case Subtract -> "-";
                        case Multiply -> "*";
                        case Divide -> "/";
                        case Comparison -> "==";
                        case GreaterThan -> ">";
                        case LessThan -> "<";
                        case GreaterThanOrEqual -> ">=";
                        case LessThanOrEqual -> "<=";
                        default -> throw new InternalException("Unknown operator");
                    });
        }

        return output.toString();
    }

    public String compile_if_statement(IfStatement if_statement) throws InternalException
    {
        String output = "";
        output += "if";
        output += "(";
        output += compile_expression(if_statement.condition);
        output += ")\n";
        output += compile_block(if_statement.block);
        if (if_statement.else_block != null)
        {
            output += "else\n";
            output += compile_block(if_statement.else_block);
        }
        return output;
    }
}
