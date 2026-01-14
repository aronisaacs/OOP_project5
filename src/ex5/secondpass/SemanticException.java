package ex5.secondpass;

import ex5.main.SJavaParseException;

/**
 * Thrown when s-Java code is syntactically correct but logically illegal.
 * @author ron.stein
 */
public class SemanticException extends SJavaParseException {
	public SemanticException(String message) {
		super(message);
	}
}