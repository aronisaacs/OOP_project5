package ex5.main;

import ex5.firstpass.ParsedLine;
import ex5.secondpass.SecondPassParser;
import ex5.firstpass.FirstPassParser;
import ex5.parser.SJavaParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ex5.parser.SJavaParseException.ParseExceptionHandler;

/**
 * Entry point for the S-Java validator.
 * Usage: java Sjavac <source-file>
 */
public class Sjavac {

    /**
     * Main method to run the S-Java validator.
     * @param args Command line arguments; expects a single argument specifying the path to the S-Java source file.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Sjavac <path-to-sjava-file>");
            System.exit(1);
        }
        String filePath = args[0];

        // Read the file and perform parsing. Handle exceptions and print appropriate exit codes.
        // Exit codes:
        // 0 - Valid S-Java code
        // 1 - Invalid S-Java code (parsing error)
        // 2 - I/O error (file not found, etc.)
		//todo maybe other exit codes for other errors?
        try {
			List<String> lines = Files.readAllLines(Paths.get(filePath));
            parseFile(lines);
        } catch (SJavaParseException e) {
            // Parsing error
            ParseExceptionHandler(e);
        }
        catch (IOException e) {
            // I/O error
            IOExceptionHandler(e, filePath);
        }
    }

    /*
        * Parses the S-Java source file at the given path.
        * @param filePath Path to the S-Java source file.
        * @throws SJavaParseException If a parsing error occurs.
     */
    private static void parseFile(List<String> lines) throws SJavaParseException {

        final List<ParsedLine> globalLines = new ArrayList<>();
        final List<ParsedLine> methodSignatures = new ArrayList<>();
        final List<List<ParsedLine>> methodLines = new ArrayList<>();
        FirstPassParser firstPassParser = new FirstPassParser(globalLines, methodSignatures, methodLines);
        firstPassParser.parse(lines);
        SecondPassParser secondPassParser = new SecondPassParser(globalLines, methodSignatures, methodLines);
        secondPassParser.parse();
        System.out.println("0"); // valid
    }

    /*
        * Handles IOException by printing an error message and exiting with code 2.
        * @param e The IOException that occurred.
        * @param filePath The path to the file that caused the exception.
     */
    private static void IOExceptionHandler(IOException e, String filePath) {
        System.out.println("2"); // I/O error
        System.err.println("Failed to read file: " + filePath);
        e.printStackTrace();
        System.exit(2);
    }



}

