package pl.undefine.cbb;

import pl.undefine.cbb.ast.ParsedFile;
import pl.undefine.cbb.utils.ErrorOr;
import pl.undefine.cbb.utils.Error;

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

    public static void main(String[] args) throws IOException
    {
        for(String file_path : args)
        {
            byte[] file = Files.readAllBytes(Path.of(file_path));

            files.put(next_file_id++, file);
            int file_id = files.size() - 1;

            Lexer lexer = new Lexer(file_id, file);
            ErrorOr<List<Token>> tokens = lexer.lex_file();
            if(tokens.is_error())
                display_error(tokens.get_error());

            Parser parser = new Parser(file_id, tokens.get_value());
            ErrorOr<ParsedFile> parsedFile = parser.parse_file();
            if(parsedFile.is_error())
                display_error(parsedFile.get_error());

            Compiler compiler = new Compiler(parsedFile.get_value());
            String cpp_code = compiler.compile();
            Files.write(Path.of(Path.of(file_path).getFileName() + ".cpp"), cpp_code.getBytes());

        }
    }

    private static void display_error(Error error)
    {
        System.out.printf("Error: %s\n", error.text);
        System.out.println("-----");

        int index = 0;

        while (index <= files.get(error.span.file_id).length)
        {
            char c;
            if (index < files.get(error.span.file_id).length)
            {
                c = (char) files.get(error.span.file_id)[index];
            }
            else
            {
                c = ' ';
            }

            if ((index >= error.span.start && index < error.span.end) || (error.span.start == error.span.end && index == error.span.start))
            {
                System.out.printf("\033[1;31m%c", c);
            }
            else
            {
                System.out.printf("\033[1;0m%c", c);
            }
            index += 1;
        }
        System.out.println();
        System.out.println("-----");

        System.exit(1);
    }
}