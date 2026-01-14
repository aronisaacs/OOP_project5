package ex5.lines;

import java.util.regex.Pattern;
import ex5.firstpass.ParsedLine;
import ex5.firstpass.StrictParsers;
import ex5.firstpass.SyntaxException;
//import org.intellij.lang.annotations.Language;

/**
 * Enum representing different types of lines in a simplified Java-like language.
 * Each type is associated with a regex pattern for matching. It also provides a method
 * to classify a line and parse it strictly:
 * Usage:
 *  * <pre>
 *  *     LineType type = LineType.classify(line);
 *  *     ParsedLine parsed = type.parseStrict(line);
 *  * </pre>
 *
 *
 * The order of the enum constants matters for correct classification.
 * Lines that do not match any type will throw an IllegalArgumentException.
 * Lines must end with ';', '{', or '}' to be considered valid.
 * @see ParsedLine
 * @see LineTypeFactory
 * @author Aron Isaacs
 */
public enum LineType {


    /**
     * Matches empty lines (whitespace only).
     */
    EMPTY("^\\s*$", (line, lineNumber) -> null),

    /**
     * Matches single-line comments starting with //, even with leading whitespace (which will throw an exception
     * in strict mode).
     */
    COMMENT("^\\s*//.*$", (line, lineNumber) -> null),




    /**
     * Matches method declarations, e.g., "void myMethod(int a) {".
     */
    METHOD_DECLARATION("^\\s*void\\s+[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*\\{\\s*$",
			StrictParsers::parseMethodDeclaration),

    /**
     * Matches if or while statements, e.g., "if (condition) {" or "while (condition) {".
     */
    IF_WHILE("^\\s*(if|while)\\s*\\([^)]*\\)\\s*\\{\\s*$",
			StrictParsers::parseIfWhile),

    /**
     * Matches return statements, e.g., "return;".
     */
    RETURN("^\\s*return\\s*;\\s*$",
			StrictParsers::parseReturn),

    /**
     * Matches closing brackets "}" possibly with leading/trailing whitespace.
     */
    CLOSING_BRACKET("^\\s*}\\s*$", StrictParsers::parseClosingBracket),

    /**
     * Matches method calls, e.g., "myMethod(5, "test");".
     */
    METHOD_CALL("^\\s*[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*;\\s*$",
			StrictParsers::parseMethodCall),
    /**
     * Matches variable assignments, e.g., "x = 10;" or "a = 5, b = 6;".
     */
    VARIABLE_ASSIGNMENT(
			"^\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+(\\s*,\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+)*\\s*;\\s*$",
			StrictParsers::parseVariableAssignment),

	/**
	 * Matches final variable declarations, e.g., "final int x = 5;".
	 */
	FINAL_VAR_DECLARATION("^\\s*final\\s+\\w+\\s+.+;\\s*$",
			StrictParsers::parseFinalVarDeclaration),

	/**
	 * Matches non-final variable declarations, e.g., "int x;" or "String name = "Alice";".
	 * purposefully placed after FINAL_VAR_DECLARATION to ensure correct matching order.
	 */
	NON_FINAL_VAR_DECLARATION(
			"^\\s*\\w+\\s+[A-Za-z]\\w*(\\s*=.*)?(\\s*,\\s*[A-Za-z]\\w*(\\s*=.*)?)*\\s*;\\s*$",
			StrictParsers::parseNonFinalVarDeclaration);


    // Regex pattern for matching lines of this type.
    private final Pattern pattern;
    // Parser function for strictly parsing lines of this type.
    private final StrictParser parser;


    /*
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
	 *
	 * @param line       The line to parse.
	 * @param lineNumber The line number in the source file.
	 * @return A ParsedLine object representing the parsed line.
	 * @throws IllegalArgumentException if the line does not conform to the expected format.
	 */
    public ParsedLine parseStrict(String line, int lineNumber) throws SyntaxException {
        return parser.parse(line, lineNumber);
    }

    // Functional interface for strict parsing of lines.
    @FunctionalInterface
    private interface StrictParser {
        ParsedLine parse(String line, int lineNumber) throws SyntaxException;
    }

}



