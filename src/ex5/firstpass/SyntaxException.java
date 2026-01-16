package ex5.firstpass;

import ex5.main.SJavaParseException;

/**
 * Thrown when s-Java code violates structural/formatting rules.
 * @author ron.stein
 */
public class SyntaxException extends SJavaParseException {

	/**
	 * Constructor for SyntaxException.
	 * @param message the detail message for the exception
	 */
	public SyntaxException(String message) {
		super(message);
	}
}