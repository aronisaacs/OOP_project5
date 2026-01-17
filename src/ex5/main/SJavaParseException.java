package ex5.main;

/**
 * Exception thrown for any parsing or validation failure in S-Java.
 * For example, if a variable is used before being declared,
 * or if a method is called with the wrong number of arguments.
 * This exception indicates that the S-Java code is invalid.
 * @author Aron Isaacs
 */
public class SJavaParseException extends RuntimeException {

	private static final String COLON_LITERAL = ": ";
	private static final String ONE_LITERAL = "1";

	/**
	 * Constructor for SJavaParseException.
	 *
	 * @param message the detail message for the exception
	 */
	public SJavaParseException(String message) {
		super(message);
	}

	/**
	 * Handles SJavaParseException by printing an error message and exiting with code 1.
	 * @param e The SJavaParseException that occurred.
	 */
	public static void parseExceptionHandler(SJavaParseException e) {
		System.out.println(ONE_LITERAL); // invalid
		System.err.println(e.getClass().getSimpleName() + COLON_LITERAL + e.getMessage());
	}
}

