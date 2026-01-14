package ex5.firstpass;

import ex5.main.SJavaParseException;

/**
 * Thrown when s-Java code violates structural/formatting rules.
 */
public class SyntaxException extends SJavaParseException {
	public SyntaxException(String message) {
		super(message);
	}
}