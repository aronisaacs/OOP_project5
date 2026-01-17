package ex5.main;

import ex5.firstpass.ParsedLine;
import ex5.secondpass.SecondPassParser;
import ex5.firstpass.FirstPassParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static ex5.main.SJavaParseException.parseExceptionHandler;

/**
 * Main class for the S-Java compiler.
 * Validates S-Java source files for syntax and semantic correctness.
 * Exits with code 0 if valid, 1 for parse errors, and 2 for I/O or usage errors.
 * @author ron.stein
 */
public class Sjavac {

	private static final String USAGE_ERROR_MESSAGE = "Usage Error: the number of arguments is invalid";
	private static final String SJAVA_ENDING = ".sjava";
	private static final String SJAVA_EXTENSION_ERROR_MESSAGE =
			"Usage Error: the file must have a .sjava extension";
	private static final String IO_ERROR_MESSAGE = "I/O Error, failed to read file: ";
	private static final String TWO_LITERAL = "2";
	private static final String ZERO_LITERAL = "0";

	/**
	 * Main method to run the S-Java validator.
	 * @param args Command line arguments;
	 * should contain exactly one argument: the path to the S-Java source file.
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				throw new IllegalArgumentException(USAGE_ERROR_MESSAGE);
			}
			String filePath = args[0];
			if (!filePath.endsWith(SJAVA_ENDING)) {
				throw new IllegalArgumentException(SJAVA_EXTENSION_ERROR_MESSAGE);
			}
			List<String> lines = Files.readAllLines(Paths.get(filePath));
			parseFile(lines);
			System.out.println(ZERO_LITERAL);
		} catch (SJavaParseException e) {
			parseExceptionHandler(e);
		} catch (IllegalArgumentException e) {
			handleGeneralError(e);
		} catch (IOException e) {
			handleIOError(e);
		}
	}

	/**
	 * Handles IOExceptions by printing an error message and exiting with code 2.
	 */
	private static void handleIOError(IOException e) {
		System.out.println(TWO_LITERAL);
		System.err.println(IO_ERROR_MESSAGE + e.getMessage());
	}

	/**
	 * Handles general exceptions by printing an error message and exiting with code 2.
	 */
	private static void handleGeneralError(Exception e) {
		System.out.println(TWO_LITERAL);
		String message = e.getMessage() != null ? e.getMessage() : e.toString();
		System.err.println(message);
	}

	/**
	 * Parses the S-Java source file at the given path.
	 * @param lines List of lines from the S-Java source file.
	 * @throws SJavaParseException if a syntax or semantic error is found.
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

