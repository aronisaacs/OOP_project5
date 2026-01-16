package ex5.secondpass;

import ex5.main.SJavaParseException;

/**
 * Thrown when s-Java code is syntactically correct but logically illegal.
 * @author ron.stein
 */
public class SemanticException extends SJavaParseException {

	/**
	 * Constructor for SemanticException.
	 * @param message the detail message for the exception
	 */
	public SemanticException(String message) {
		super(message);
	}
}