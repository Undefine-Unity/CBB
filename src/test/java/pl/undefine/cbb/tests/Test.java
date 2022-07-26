package pl.undefine.cbb.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test
{
    private static int ignored = 0;
    private static int failed = 0;
    private static final List<String> failed_list = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException
    {
        List<String> tests;
        try (Stream<Path> walk = Files.walk(Paths.get("./examples/")))
        {
            tests = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("cbb"))
                    .collect(Collectors.toList());
        }

        if (args.length != 1)
        {
            display_help();
            return;
        }

        if (args[0].equals("regenerate"))
        {
            for (String test : tests)
            {
                regenerate_test(test);
            }
        }
        else if (args[0].equals("test"))
        {
            for (String test : tests)
            {
                run_test(test);
            }

            System.out.println("Passed tests: " + (tests.size() - failed - ignored));
            System.out.println("Ignored tests: " + ignored);
            System.out.println("Failed tests: " + failed);
            for (String test : failed_list)
            {
                System.out.println("    " + test);
            }

            if (failed == 0)
            {
                System.exit(0);
            }
            else
            {
                System.exit(1);
            }
        }
        else
        {
            display_help();
        }
    }

    private static void display_help()
    {
        System.out.println("Usage: ");
        System.out.println("  regenerate - regenerates all test outputs");
        System.out.println("  test - runs all of the tests");
    }

    private static void regenerate_test(String test_path) throws IOException
    {
        Files.write(Path.of(test_path + ".out"), build_and_run(test_path).getBytes());
    }

    private static void run_test(String test_path) throws IOException, InterruptedException
    {
        Path test_path2 = Path.of(test_path + ".out");
        if (Files.exists(test_path2))
        {
            byte[] expected = Files.readAllBytes(test_path2);
            byte[] actual = build_and_run(test_path).getBytes();
            if (!Arrays.equals(expected, actual))
            {
                failed++;
                failed_list.add(test_path);
                System.out.println("[ERROR] Unexpected output");
                System.out.println("  Expected:");
                System.out.println("    stdout: \n" + new String(expected));
                System.out.println("  Actual:");
                System.out.println("    stdout: \n" + new String(actual));
            }
        }
        else
        {
            System.out.println("[WARNING] Could not find any input/output data for " + test_path + ". Ignoring testing. Only checking if it compiles.");
            String executable_path = test_path.substring(0, test_path.length() - 4);
            System.out.println("[CMD] java -jar CBB.jar " + test_path);
            Process compiler_process = new ProcessBuilder("java", "-jar", "CBB.jar", test_path).start();
            compiler_process.waitFor();
            if (compiler_process.exitValue() != 0)
            {
                failed++;
                failed_list.add(test_path);
            }
            else
            {
                System.out.println("[CMD] clang++ -std=c++2a " + test_path + ".cpp" + " -o " + executable_path);
                Process clang_process = new ProcessBuilder("clang++", "-std=c++2a", "-I.", test_path + ".cpp", "-o", executable_path).start();
                clang_process.waitFor();
                if (clang_process.exitValue() != 0)
                {
                    failed++;
                    failed_list.add(test_path);
                }
                else
                {
                    ignored++;
                }
            }
        }
    }

    private static String build_and_run(String file_path)
    {
        String executable_path = file_path.substring(0, file_path.length() - 4);
        StringBuilder output = new StringBuilder();
        try
        {
            System.out.println("[CMD] java -jar CBB.jar " + file_path);
            Process compiler_process = new ProcessBuilder("java", "-jar", "CBB.jar", file_path).start();
            while (compiler_process.isAlive())
            {
                if (compiler_process.getInputStream().available() > 0)
                    output.append(new String(compiler_process.getInputStream().readAllBytes()));
            }

            if (compiler_process.exitValue() == 0)
            {
                System.out.println("[CMD] clang++ -std=c++2a " + file_path + ".cpp" + " -o " + executable_path);
                Process clang_process = new ProcessBuilder("clang++", "-std=c++2a", "-I.", "-Wno-writable-strings", file_path + ".cpp", "-o", executable_path).start();
                clang_process.waitFor();
                if (clang_process.exitValue() == 0)
                {
                    System.out.println("[CMD] " + executable_path);
                    Process process = new ProcessBuilder(executable_path).start();
                    while (process.isAlive())
                    {
                        if (process.getInputStream().available() > 0)
                            output.append(new String(process.getInputStream().readAllBytes()));
                    }
                }
                else
                {
                    System.out.println("[ERROR] Failed to compile " + file_path);
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            new File(file_path + ".cpp").delete();
            new File(executable_path).delete();
        }
        return output.toString();
    }
}
