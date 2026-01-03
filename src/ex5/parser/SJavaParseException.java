package ex5.parser;

/**
 * Exception thrown for any parsing or validation failure in S-Java.
 * For example, if a variable is used before being declared, or if a method is called with the wrong number of arguments.
 * This exception indicates that the S-Java code is invalid.
 * @author Aron Isaacs
 */
public class SJavaParseException extends RuntimeException {

    public SJavaParseException(String message) {
        super(message);
    }

	/*
	 * Handles SJavaParseException by printing an error message and exiting with code 1.
	 * @param e The SJavaParseException that occurred.
	 */
	public static void ParseExceptionHandler(SJavaParseException e) {
		System.out.println("1"); // invalid
		System.err.println("S-Java parse error: " + e.getMessage());
		e.printStackTrace();
		System.exit(0);
	}
}

