package pl.undefine.cbb;

import pl.undefine.cbb.ast.ParsedFile;
import pl.undefine.cbb.utils.InternalException;
import pl.undefine.cbb.utils.LexerException;
import pl.undefine.cbb.utils.ParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
    public static final Map<Integer, byte[]> files = new HashMap<>();
    public static int next_file_id = 0;

    public static void main(String[] args)
    {
        try
        {
            for (String file_path : args)
            {
                Path path = Path.of(file_path);

                byte[] file = Files.readAllBytes(path);

                files.put(next_file_id++, file);
                int file_id = files.size() - 1;

                Lexer lexer = new Lexer(file_id, file);
                List<Token> tokens = lexer.lex_file();
                //Lexer.dump_tokens(tokens);

                Parser parser = new Parser(file_id, tokens);
                ParsedFile parsed_file = parser.parse_file();
                //ASTDumper.dump(parsed_file);

                Compiler compiler = new Compiler(parsed_file);
                String cpp_code = compiler.compile();
                Files.write(Path.of(path + ".cpp"), cpp_code.getBytes());
            }
        }
        catch (IOException e)
        {
            System.err.println("IO Error: " + e);
            System.exit(3);
        }
        catch (InternalException e)
        {
            if (is_debug())
            {
                e.printStackTrace();
            }
            System.err.println("Compiler internal error");
            System.exit(2);
        }
        catch (LexerException e)
        {
            if (is_debug())
            {
                e.printStackTrace();
            }
            display_error_body(e.getMessage(), e.span);
        }
        catch (ParserException e)
        {
            if (is_debug())
            {
                e.printStackTrace();
            }
            display_error_body(e.getMessage(), e.span);
        }
    }

    /**
     * This functions checks if debugging is enabled by simply
     * checking the fact that assertions are enabled using the `-ea` JVM flag
     *
     * @return is debugging enabled
     */
    public static boolean is_debug()
    {
        try
        {
            assert false;
        }
        catch (AssertionError e)
        {
            return true;
        }
        return false;
    }

    private static void display_error_body(String message, Span span)
    {
        System.err.printf("Error: %s\n", message);
        System.err.println("-----");

        int index = 0;

        while (index <= files.get(span.file_id).length)
        {
            char c;
            if (index < files.get(span.file_id).length)
            {
                c = (char) files.get(span.file_id)[index];
            }
            else
            {
                c = ' ';
            }

            if ((index >= span.start && index < span.end) || (span.start == span.end && index == span.start))
            {
                System.err.printf("\033[1;31m%c", c);
            }
            else
            {
                System.err.printf("\033[1;0m%c", c);
            }
            index += 1;
        }
        System.err.println();
        System.err.println("-----");

        System.exit(1);
    }
}