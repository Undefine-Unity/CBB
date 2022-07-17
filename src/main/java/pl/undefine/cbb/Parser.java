package pl.undefine.cbb;

import pl.undefine.cbb.ast.Number;
import pl.undefine.cbb.ast.*;
import pl.undefine.cbb.utils.ParserException;

import java.util.List;

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

    ParsedFile parse_file() throws ParserException
    {
        ParsedFile parsed_file = new ParsedFile(file_id);

        while (index < tokens.size())
        {
            if (tokens.get(index).type == TokenType.Name)
            {
                if (Type.is_type(tokens.get(index)))
                {
                    parsed_file.declarations.add(parse_declaration());
                }
                else
                {
                    throw new ParserException("unexpected token", tokens.get(index).span);
                }
            }
            else if (tokens.get(index).type == TokenType.Eof)
            {
                // We have reached the end of the file
                break;
            }
            else
            {
                throw new ParserException("unexpected token", tokens.get(index).span);
            }
        }

        return parsed_file;
    }

    Declaration parse_declaration() throws ParserException
    {
        index++; // Skip the type for now
        if (index < tokens.size())
        {
            if (tokens.get(index).type == TokenType.Name)
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
                throw new ParserException("expected name", tokens.get(index).span);
            }
        }
        else
        {
            throw new ParserException("incomplete declaration", tokens.get(index - 1).span);
        }
    }

    Function parse_function() throws ParserException
    {
        Function function = new Function();
        // We do not do any checks because everything is checked in `parse_declaration`
        function.return_type = Type.get_type(get_value_and_advance());
        function.name = get_value_and_advance();
        // Skip parentheses for now
        expect(TokenType.LParen);
        expect(TokenType.RParen);
        function.block = parse_block();
        return function;
    }

    Variable parse_variable() throws ParserException
    {
        Variable variable = new Variable();
        // We do not do any checks because everything is checked in `parse_declaration`
        variable.type = Type.get_type(get_value_and_advance());
        variable.name = get_value_and_advance();
        // This variable is assigned during declaration
        if(tokens.get(index).type == TokenType.Equals)
        {
            index++;
            variable.asignment = parse_expression();
        }
        return variable;
    }

    Block parse_block() throws ParserException
    {
        Block block = new Block();
        expect(TokenType.LCurly);
        while (index < tokens.size())
        {
            if (tokens.get(index).type == TokenType.RCurly)
            {
                index++;
                return block;
            }
            else if (tokens.get(index).type == TokenType.Semicolon)
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

    Statement parse_statement() throws ParserException
    {
        if(Type.is_type(tokens.get(index)))
            return parse_declaration();
        else
            return parse_expression();
    }

    Expression parse_expression() throws ParserException
    {
        if (tokens.get(index).type == TokenType.Name)
        {
            if(tokens.get(index+1).type == TokenType.LParen)
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
        else if (tokens.get(index).type == TokenType.String)
        {
            return new StringLiteral(get_value_and_advance());
        }
        else if (tokens.get(index).type == TokenType.Number)
        {
            return new Number(Long.parseLong(get_value_and_advance()));
        }
        else
        {
            throw new ParserException("invalid or unsupported expression", tokens.get(index).span);
        }
    }

    Call parse_call() throws ParserException
    {
        Call call = new Call();
        if (tokens.get(index).type == TokenType.Name)
        {
            call.name = tokens.get(index).value;
            index++;
            if (index >= tokens.size() || tokens.get(index).type != TokenType.LParen)
            {
                index++;
                throw new ParserException("expected '('", tokens.get(index).span);
            }
            index++;
            while (index < tokens.size())
            {
                if (tokens.get(index).type == TokenType.RParen)
                {
                    index++;
                    return call;
                }
                else if (tokens.get(index).type == TokenType.String)
                {
                    call.params.add(new StringLiteral(get_value_and_advance()));
                }
                else if (tokens.get(index).type == TokenType.Number)
                {
                    call.params.add(new Number(Long.parseLong(get_value_and_advance())));
                }
                else if (tokens.get(index).type == TokenType.Name)
                {
                    // Let's assume that this is a variable name before we have a typechecker
                    call.params.add(new VariableValue(get_value_and_advance()));
                }
                else
                {
                    throw new ParserException("unexpected token", tokens.get(index).span);
                }
            }
            throw new ParserException("expected ')'", tokens.get(index - 1).span);
        }
        else
        {
            throw new ParserException("expected function call", tokens.get(index).span);
        }
    }

    public String get_value_and_advance()
    {
        index++;
        return tokens.get(index - 1).value;
    }

    public void expect(TokenType token_type) throws ParserException
    {
        if (tokens.get(index).type != token_type)
        {
            throw new ParserException("expected `" + token_type.name() + "`", tokens.get(index).span);
        }
        index++;
    }
}
