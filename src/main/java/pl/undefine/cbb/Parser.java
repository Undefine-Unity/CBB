package pl.undefine.cbb;

import pl.undefine.cbb.ast.*;
import pl.undefine.cbb.ast.Number;
import pl.undefine.cbb.utils.Error;
import pl.undefine.cbb.utils.ErrorOr;

import java.util.List;

import static pl.undefine.cbb.Token.TokenType.Semicolon;

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

    ErrorOr<ParsedFile> parse_file()
    {
        ParsedFile parsed_file = new ParsedFile(file_id);

        while (index < tokens.size())
        {
            if (tokens.get(index).type == Token.TokenType.Name)
            {
                if (tokens.get(index).value.equals("int") || tokens.get(index).value.equals("void"))
                {
                    ErrorOr<Function> function = parse_function();
                    if(function.is_error())
                        return function.rethrow();
                    parsed_file.functions.add(function.get_value());
                }
                else
                {
                    return new ErrorOr<>(new Error("unexpected token", tokens.get(index).span));
                }
            }
            else if (tokens.get(index).type == Token.TokenType.Eof)
            {
                break;
            }
            else
            {
                return new ErrorOr<>(new Error("unexpected token", tokens.get(index).span));
            }
        }

        return new ErrorOr<>(parsed_file);
    }

    ErrorOr<Function> parse_function()
    {
        Function function = new Function();
        if (index < tokens.size())
        {
            function.return_type = tokens.get(index).value;
            index++;
            if (tokens.get(index).type == Token.TokenType.Name)
            {
                function.name = tokens.get(index).value;
                index += 3;
                ErrorOr<Block> block = parse_block();
                if(block.is_error())
                    return block.rethrow();
                function.block = block.get_value();
            }
            else
            {
                return new ErrorOr<>(new Error("expected function name", tokens.get(index).span));
            }
        }
        else
        {
            return new ErrorOr<>(new Error("incomplete function definition", tokens.get(index - 1).span));
        }
        return new ErrorOr<>(function);
    }

    ErrorOr<Block> parse_block()
    {
        Block block = new Block();
        index++;
        while (index < tokens.size())
        {
            if(tokens.get(index).type == Token.TokenType.RCurly)
            {
                index++;
                return new ErrorOr<>(block);
            }
            else if(tokens.get(index).type == Semicolon)
            {
                index++;
            }
            else
            {
                ErrorOr<Statement> statement = parse_statement();
                if(statement.is_error())
                    return statement.rethrow();
                block.statements.add(statement.get_value());
            }
        }
        return new ErrorOr<>(new Error("incomplete block", tokens.get(index - 1).span));
    }

    ErrorOr<Statement> parse_statement()
    {
        ErrorOr<Expression> expression = parse_expression();
        if(expression.is_error())
            return expression.rethrow();
        return new ErrorOr<>(expression.get_value());
    }

    ErrorOr<Expression> parse_expression()
    {
        if(tokens.get(index).type == Token.TokenType.Name)
        {
            ErrorOr<Call> call = parse_call();
            if(call.is_error())
                return call.rethrow();
            return new ErrorOr<>(call.get_value());
        }
        else if(tokens.get(index).type == Token.TokenType.String)
        {
            return new ErrorOr<>(new StringLiteral(tokens.get(index).value));
        }
        else if(tokens.get(index).type == Token.TokenType.Number)
        {
            return new ErrorOr<>(new Number(Long.parseLong(tokens.get(index).value)));
        }
        else
        {
            return new ErrorOr<>(new Error("invalid or unsupported expression", tokens.get(index).span));
        }
    }

    ErrorOr<Call> parse_call()
    {
        Call call = new Call();
        if(tokens.get(index).type == Token.TokenType.Name)
        {
            call.name = tokens.get(index).value;
            index++;
            if(index >= tokens.size() || tokens.get(index).type != Token.TokenType.LParen)
            {
                index++;
                return new ErrorOr<>(new Error("expected '('", tokens.get(index).span));
            }
            index++;
            while(index < tokens.size())
            {
                if(tokens.get(index).type == Token.TokenType.RParen)
                {
                    index++;
                    return new ErrorOr<>(call);
                }
                else if(tokens.get(index).type == Token.TokenType.String)
                {
                    call.params.add(new StringLiteral(tokens.get(index).value));
                    index++;
                }
                else if(tokens.get(index).type == Token.TokenType.Number)
                {
                    call.params.add(new Number(Long.parseLong(tokens.get(index).value)));
                    index++;
                }
            }
            return new ErrorOr<>(new Error("expected ')'", tokens.get(index - 1).span));
        }
        else
        {
            return new ErrorOr<>(new Error("expected function call", tokens.get(index).span));
        }
    }
}
