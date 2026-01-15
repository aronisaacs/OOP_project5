package ex5.main;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.SyntaxException;
import ex5.secondpass.SecondPassParser;
import ex5.firstpass.FirstPassParser;
import ex5.secondpass.SemanticException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ex5.main.SJavaParseException.ParseExceptionHandler;

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
		try {
			if (args.length != 1) {
				throw new IllegalArgumentException("Usage Error: exactly one argument is required " +
						"(java Sjavac <source-file>)");
			}
			String filePath = args[0];
			if(!filePath.endsWith(".sjava")) {
				throw new IllegalArgumentException("Usage Error: the file must have a .sjava extension");
			}
			List<String> lines = Files.readAllLines(Paths.get(filePath));
			parseFile(lines);
			System.out.println("0");
		} catch (SJavaParseException e) {
            ParseExceptionHandler(e);
        } catch (IllegalArgumentException e) {
            // I/O error
            handleGeneralError(e);
        } catch (IOException e){
			handleIOError(e);
		}
    }

	private static void handleIOError(IOException e) {
		System.out.println("2");
		System.err.println("I/O Error, failed to read file: " + e.getMessage());
//		System.exit(2);
	}

	private static void handleGeneralError(Exception e) {
		System.out.println("2");
		String message = e.getMessage() != null ? e.getMessage() : e.toString();
		System.err.println( message);
//		System.exit(2);
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
    }
}

