package pl.undefine.cbb;

import pl.undefine.cbb.ast.Number;
import pl.undefine.cbb.ast.*;
import pl.undefine.cbb.utils.InternalException;
import pl.undefine.cbb.utils.ParserException;

import java.util.List;
import java.util.Stack;

public class Parser
{
    int file_id;
    List<Token> tokens;
    int index;

    public Parser(int file_id, List<Token> tokens)
    {
        this.file_id = file_id;
        this.tokens = tokens;
    }

    ParsedFile parse_file() throws ParserException, InternalException
    {
        ParsedFile parsed_file = new ParsedFile(file_id);

        while (index < tokens.size())
        {
            if (current().type == TokenType.Name)
            {
                if (Type.is_type(current()))
                {
                    parsed_file.declarations.add(parse_declaration());
                }
                else
                {
                    throw new ParserException("unexpected token", current().span);
                }
            }
            else if (current().type == TokenType.Eof)
            {
                // We have reached the end of the file
                break;
            }
            else
            {
                throw new ParserException("unexpected token", current().span);
            }
        }

        return parsed_file;
    }

    Declaration parse_declaration() throws ParserException, InternalException
    {
        index++; // Skip the type for now
        if (index < tokens.size())
        {
            if (current().type == TokenType.Name)
            {
                boolean function; // True - function, False - variable
                function = tokens.get(index + 1).type == TokenType.LParen; // If the next token is a `(` this is a function declaration
                index--; // Go back to the type
                if (function)
                    return parse_function();
                else
                    return parse_variable();
            }
            else
            {
                throw new ParserException("expected name", current().span);
            }
        }
        else
        {
            throw new ParserException("incomplete declaration", tokens.get(index - 1).span);
        }
    }

    FunctionDeclaration parse_function() throws ParserException, InternalException
    {
        FunctionDeclaration function_declaration = new FunctionDeclaration();
        // We do not do any checks because everything is checked in `parse_declaration`
        function_declaration.return_type = Type.get_type(get_value_and_advance());
        function_declaration.name = get_value_and_advance();
        // Skip parentheses for now
        expect(TokenType.LParen);
        while(index < tokens.size())
        {
            if(current().type == TokenType.RParen)
            {
                index++;
                break;
            }
            else if(current().type == TokenType.Name && Type.is_type(current()))
            {
                VariableDeclaration parameter_declaration = new VariableDeclaration();
                parameter_declaration.type = Type.get_type(get_value_and_advance());
                if(current().type != TokenType.Name)
                    throw new ParserException("invalid function parameter", current().span);

                parameter_declaration.name = get_value_and_advance();

                if(current().type != TokenType.RParen)
                {
                    expect(TokenType.Comma);
                }
                function_declaration.parameters.add(parameter_declaration);
            }
            else
            {
                throw new ParserException("unexpected token", current().span);
            }
        }
        function_declaration.block = parse_block();
        return function_declaration;
    }

    VariableDeclaration parse_variable() throws ParserException, InternalException
    {
        VariableDeclaration variable_declaration = new VariableDeclaration();
        // We do not do any checks because everything is checked in `parse_declaration`
        variable_declaration.type = Type.get_type(get_value_and_advance());
        variable_declaration.name = get_value_and_advance();
        // This variable is assigned during declaration
        if (current().type == TokenType.Equals)
        {
            index++;
            variable_declaration.asignment = parse_expression();
        }
        // We ignore the semicolon for now, I think later they should be a requirement
        if(current().type == TokenType.Semicolon)
        {
            index++;
        }
        return variable_declaration;
    }

    Block parse_block() throws ParserException, InternalException
    {
        Block block = new Block();
        expect(TokenType.LCurly);
        while (index < tokens.size())
        {
            if (current().type == TokenType.RCurly)
            {
                index++;
                return block;
            }
            else if (current().type == TokenType.Semicolon)
            {
                // Semicolons are not required, but they're not a mistake either, so we just ignore them
                index++;
            }
            else
            {
                block.statements.add(parse_statement());
            }
        }
        throw new ParserException("incomplete block", tokens.get(index - 1).span);
    }

    Statement parse_statement() throws ParserException, InternalException
    {
        if (Type.is_type(current()))
            return parse_declaration();
        else if (current().type == TokenType.Name && current().value.equals("if"))
            return parse_if_statement();
        else if (current().type == TokenType.Name && current().value.equals("return"))
        {
            index++;
            ReturnStatement return_statement = new ReturnStatement();
            return_statement.value = parse_expression();
            expect(TokenType.Semicolon);
            return return_statement;
        }
        else
            return parse_expression();
    }

    Expression parse_expression() throws ParserException, InternalException
    {
        Stack<Expression> expression_stack = new Stack<>();
        int last_precedence = Integer.MAX_VALUE;

        Expression left_side = parse_operand();
        expression_stack.push(left_side);

        while(index < tokens.size())
        {
            Operator operator = parse_operator();
            if(operator == null)
            {
                index--;
                break;
            }

            int precedence = operator.get_precedence();

            if(index == tokens.size())
            {
                throw new ParserException("incomplete math expression", tokens.get(index - 1).span);
            }

            Expression right_side = parse_operand();

            while (precedence <= last_precedence && expression_stack.size() > 1)
            {
                Expression right_side2 = expression_stack.pop();
                Expression operator2 = expression_stack.pop();

                last_precedence = ((Operator)operator2).get_precedence();

                if (last_precedence < precedence) {
                    expression_stack.push(operator2);
                    expression_stack.push(right_side2);
                    break;
                }

                Expression left_side2 = expression_stack.pop();

                expression_stack.push(new BinaryOperation(left_side2, operator2, right_side2));
            }

            expression_stack.push(operator);
            expression_stack.push(right_side);

            last_precedence = precedence;
        }

        while (expression_stack.size() != 1)
        {
            Expression right_side2 = expression_stack.pop();
            Expression operator2 = expression_stack.pop();
            Expression left_side2 = expression_stack.pop();

            expression_stack.push(new BinaryOperation(left_side2, operator2, right_side2));
        }

        return expression_stack.pop();
    }

    Expression parse_operand() throws ParserException, InternalException
    {
        if (current().type == TokenType.Name)
        {
            if (tokens.get(index + 1).type == TokenType.LParen)
            {
                // Next token is a `(`, so this is a function call
                return parse_call();
            }
            else
            {
                // Otherwise we are referencing a variable
                // Let's assume that this is a variable name before we have a typechecker
                return new VariableValue(get_value_and_advance());
            }
        }
        else if (current().type == TokenType.String)
        {
            return new StringLiteral(get_value_and_advance());
        }
        else if (current().type == TokenType.Number)
        {
            return new Number(Long.parseLong(get_value_and_advance()));
        }
        else
        {
            throw new ParserException("invalid or unsupported expression", current().span);
        }
    }

    Operator parse_operator() throws ParserException
    {
        return switch (current_advance().type)
        {
            case Plus -> new Operator(OperatorType.Add);
            case Minus -> new Operator(OperatorType.Subtract);
            case Asterisk -> new Operator(OperatorType.Multiply);
            case ForwardSlash -> new Operator(OperatorType.Divide);
            case DoubleEquals -> new Operator(OperatorType.Comparison);
            case GreaterThan -> new Operator(OperatorType.GreaterThan);
            case LessThan -> new Operator(OperatorType.LessThan);
            case GreaterThanOrEqual -> new Operator(OperatorType.GreaterThanOrEqual);
            case LessThanOrEqual -> new Operator(OperatorType.LessThanOrEqual);
            default -> null;
        };
    }

    IfStatement parse_if_statement() throws ParserException, InternalException
    {
        IfStatement if_statement = new IfStatement();
        index++; // Skip `if` token
        expect(TokenType.LParen);
        if_statement.condition = parse_expression();
        expect(TokenType.RParen);
        if_statement.block = parse_block();
        if(current().type == TokenType.Name && current().value.equals("else"))
        {
            index++; // Skip `else`
            if_statement.else_block = parse_block();
        }
        return if_statement;
    }

    Call parse_call() throws ParserException, InternalException
    {
        Call call = new Call();
        if (current().type == TokenType.Name)
        {
            call.name = get_value_and_advance();
            expect(TokenType.LParen);
            while (index < tokens.size())
            {
                if (current().type == TokenType.RParen)
                {
                    index++;
                    return call;
                }
                else
                {
                    call.parameters.add(parse_expression());
                }
            }
            throw new ParserException("expected ')'", tokens.get(index - 1).span);
        }
        else
        {
            throw new ParserException("expected function call", current().span);
        }
    }

    private Token current()
    {
        return tokens.get(index);
    }

    private Token current_advance()
    {
        index++;
        return tokens.get(index-1);
    }

    private String get_value_and_advance()
    {
        index++;
        return tokens.get(index - 1).value;
    }

    private void expect(TokenType token_type) throws ParserException
    {
        if (current().type != token_type)
        {
            throw new ParserException("expected `" + token_type.name() + "`", current().span);
        }
        index++;
    }
}
