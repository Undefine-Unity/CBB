package pl.undefine.cbb;

import pl.undefine.cbb.ast.Number;
import pl.undefine.cbb.ast.*;

public class ASTDumper
{
    private static int tabs;

    public static void dump(ParsedFile parsed_file)
    {
        tabs = 0;
        System.out.printf("<ParsedFile file-id=`%d`>\n", parsed_file.file_id);
        tabs++;
        for (Declaration declaration : parsed_file.declarations)
        {
            dump_declaration(declaration);
        }
        tabs--;
    }

    private static void dump_declaration(Declaration declaration)
    {
        if (declaration instanceof FunctionDeclaration function_declaration)
        {
            dump_function(function_declaration);
        }
        else if (declaration instanceof VariableDeclaration variable_declaration)
        {
            dump_variable(variable_declaration);
        }
        else
        {
            print_tabs();
            System.out.println("<InvalidDeclaration>");
        }
    }

    private static void dump_function(FunctionDeclaration function_declaration)
    {
        print_tabs();
        System.out.printf("<FunctionDeclaration name=`%s` return_type=`%s`>\n", function_declaration.name, function_declaration.return_type.cbb_name);
        tabs++;
        for (VariableDeclaration parameter : function_declaration.parameters)
        {
            dump_variable(parameter);
        }
        dump_block(function_declaration.block);
        tabs--;
    }

    private static void dump_variable(VariableDeclaration variable_declaration)
    {
        print_tabs();
        System.out.printf("<VariableDeclaration name=`%s` type=`%s`>\n", variable_declaration.name, variable_declaration.type.cbb_name);
        tabs++;
        dump_expression(variable_declaration.asignment);
        tabs--;
    }

    private static void dump_block(Block block)
    {
        print_tabs();
        System.out.println("<Block>");
        tabs++;
        for (Statement statement : block.statements)
        {
            dump_statement(statement);
        }
        tabs--;
    }

    private static void dump_statement(Statement statement)
    {
        if (statement instanceof Declaration declaration)
        {
            dump_declaration(declaration);
        }
        else if (statement instanceof IfStatement if_statement)
        {
            print_tabs();
            System.out.println("<IfStatement>");
            tabs++;
            dump_expression(if_statement.condition);
            dump_block(if_statement.block);
            if (if_statement.else_block != null)
                dump_block(if_statement.else_block);
            tabs--;
        }
        else if (statement instanceof ReturnStatement return_statement)
        {
            print_tabs();
            System.out.println("<ReturnStatement>");
            tabs++;
            dump_expression(return_statement.value);
            tabs--;
        }
        else if (statement instanceof Expression expression)
        {
            dump_expression(expression);
        }
        else
        {
            print_tabs();
            System.out.println("<InvalidStatement>");
        }
    }

    private static void dump_expression(Expression expression)
    {
        if (expression instanceof Number number)
        {
            print_tabs();
            System.out.printf("<Number value=`%d`>\n", number.number);
        }
        else if (expression instanceof StringLiteral string_literal)
        {
            print_tabs();
            System.out.printf("<StringLiteral value=`%s`>\n", string_literal.value);
        }
        else if (expression instanceof VariableValue variable_value)
        {
            print_tabs();
            System.out.printf("<VariableValue value=`%s`>\n", variable_value.variable_name);
        }
        else if (expression instanceof Operator operator)
        {
            print_tabs();
            System.out.printf("<Operator type=`%s`>\n", operator.type.name());
        }
        else if (expression instanceof Call call)
        {
            print_tabs();
            System.out.printf("<Call name=`%s`>\n", call.name);
            tabs++;
            for (Expression parameter : call.parameters)
            {
                dump_expression(parameter);
            }
            tabs--;
        }
        else if (expression instanceof BinaryOperation binary_operation)
        {
            print_tabs();
            System.out.print("<BinaryOperation>\n");
            tabs++;
            dump_expression(binary_operation.left_side);
            dump_expression(binary_operation.operator);
            dump_expression(binary_operation.right_side);
            tabs--;
        }
        else
        {
            print_tabs();
            System.out.println("<InvalidExpression>");
        }
    }

    private static void print_tabs()
    {
        for (int i = 0; i < tabs; i++)
        {
            System.out.print("  ");
        }
    }
}
