package ex5.lines;

import java.util.regex.Pattern;
import ex5.firstpass.ParsedLine;
import ex5.firstpass.StrictParsers;
import ex5.firstpass.SyntaxException;

/**
 * Enum representing different types of lines in a simplified Java-like language.
 * Each type is associated with a regex pattern for matching. It also provides a method
 * to classify a line and parse it strictly:
 * Usage:
 * * <pre>
 *  *     LineType type = LineType.classify(line);
 *  *     ParsedLine parsed = type.parseStrict(line);
 *  * </pre>
 * <p>
 * <p>
 * The order of the enum constants matters for correct classification.
 * Lines that do not match any type will throw an IllegalArgumentException.
 * Lines must end with ';', '{', or '}' to be considered valid.
 * @author Aron Isaacs
 * @see ParsedLine
 * @see LineTypeFactory
 */
public enum LineType {


	/**
	 * Matches empty lines (whitespace only).
	 */
	EMPTY(Constants.EMPTY_REGEX, (line, lineNumber) -> null),

	/**
	 * Matches single-line comments starting with //,
	 * even with leading whitespace (which will throw an exception in strict parsing).
	 */
	COMMENT(Constants.COMMENT_REGEX, StrictParsers::parseComment),


	/**
	 * Matches method declarations, e.g., "void myMethod(int a) {".
	 */
	METHOD_DECLARATION(Constants.METHOD_DECLARATION_REGEX,
			StrictParsers::parseMethodDeclaration),

	/**
	 * Matches if or while statements, e.g., "if (condition) {" or "while (condition) {".
	 */
	IF_WHILE(Constants.IFWHILE_REGEX,
			StrictParsers::parseIfWhile),

	/**
	 * Matches return statements, e.g., "return;".
	 */
	RETURN(Constants.RETURN_REGEX,
			StrictParsers::parseReturn),

	/**
	 * Matches closing brackets "}" possibly with leading/trailing whitespace.
	 */
	CLOSING_BRACKET(Constants.CLOSING_BRACKET_REGEX, StrictParsers::parseClosingBracket),

	/**
	 * Matches method calls, e.g., "myMethod(5, "test");".
	 */
	METHOD_CALL(Constants.METHOD_CALL_REGEX,
			StrictParsers::parseMethodCall),
	/**
	 * Matches variable assignments, e.g., "x = 10;" or "a = 5, b = 6;".
	 */
	VARIABLE_ASSIGNMENT(
			Constants.VARIABLE_ASSIGNMENT_REGEX,
			StrictParsers::parseVariableAssignment),

	/**
	 * Matches final variable declarations, e.g., "final int x = 5;".
	 */
	FINAL_VAR_DECLARATION(Constants.FINAL_VAR_DECLARATION_REGEX,
			StrictParsers::parseFinalVarDeclaration),

	/**
	 * Matches non-final variable declarations, e.g., "int x;" or "String name = "Alice";".
	 * purposefully placed after FINAL_VAR_DECLARATION to ensure correct matching order.
	 */
	NON_FINAL_VAR_DECLARATION(
			Constants.NON_FINAL_VAR_DECLARATION_REGEX,
			StrictParsers::parseNonFinalVarDeclaration);

	private static final class Constants{
		public static final String FINAL_VAR_DECLARATION_REGEX = "^\\s*final\\s+\\w+\\s+.+;\\s*$";
		public static final String VARIABLE_ASSIGNMENT_REGEX =
				"^\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+(\\s*,\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+)*\\s*;\\s*$";
		public static final String METHOD_CALL_REGEX = "^\\s*[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*;\\s*$";
		public static final String CLOSING_BRACKET_REGEX = "^\\s*}\\s*$";
		public static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
		public static final String IFWHILE_REGEX = "^\\s*(if|while)\\s*\\([^)]*\\)\\s*\\{\\s*$";
		public static final String METHOD_DECLARATION_REGEX =
				"^\\s*void\\s+[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*\\{\\s*$";
		public static final String COMMENT_REGEX = "^\\s*//.*$";
		public static final String EMPTY_REGEX = "^\\s*$";
		private static final String NON_FINAL_VAR_DECLARATION_REGEX =
				"^\\s*\\w+\\s+([A-Za-z]|_\\w)\\w*(\\s*=.*)?" +
						"(\\s*,\\s*([A-Za-z]|_\\w)\\w*(\\s*=.*)?)*\\s*;\\s*$";
	}

	// Regex pattern for matching lines of this type.
	private final Pattern pattern;
	// Parser function for strictly parsing lines of this type.
	private final StrictParser parser;


	/**
	 * Constructor for LineType enum. Compiles the given regex pattern and assigns the parser function.
	 * @param regex The regex pattern as a string.
	 * @param parser The parser function to strictly parse lines of this type.
	 */
	LineType(String regex, StrictParser parser) {
		this.pattern = Pattern.compile(regex);
		this.parser = parser;
	}

	/**
	 * Gets the regex pattern associated with this line type.
	 * @return The compiled regex pattern.
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Strictly parses the given line according to this line type's parser.
	 * @param line       The line to parse.
	 * @param lineNumber The line number in the source file.
	 * @return A ParsedLine object representing the parsed line.
	 * @throws SyntaxException if the line does not conform to the expected format.
	 */
	public ParsedLine parseStrict(String line, int lineNumber) throws SyntaxException {
		return parser.parse(line, lineNumber);
	}

	/**
	 * Functional interface for strict parsing of lines.
	 */
	@FunctionalInterface
	private interface StrictParser {
		/**
		 * Strictly parses a line and returns a ParsedLine object.
		 * @param line A line of code to parse.
		 * @param lineNumber The line number in the source file.
		 * @return A ParsedLine object representing the parsed line.
		 * @throws SyntaxException if the line does not conform to the expected format.
		 */
		ParsedLine parse(String line, int lineNumber) throws SyntaxException;
	}

}



