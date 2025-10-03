package ex5.lines;

import java.util.regex.Pattern;
import ex5.Ami.ParsedLine;
import org.intellij.lang.annotations.Language;

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
    EMPTY("^\\s*$", line -> null),

    /**
     * Matches single-line comments starting with //, even with leading whitespace (which will throw an exception
     * in strict mode).
     */
    COMMENT("^\\s*//.*$", line -> null),

    /**
     * Matches final variable declarations, e.g., "final int x = 5;".
     */
    FINAL_VAR_DECLARATION("^\\s*final\\s+(int|double|boolean|char|String)\\s+.+;\\s*$",
            ParsedLine::parseFinalVarDeclaration),

    /**
     * Matches non-final variable declarations, e.g., "int x;" or "String name = "Alice";".
     */
    NON_FINAL_VAR_DECLARATION("^\\s*(int|double|boolean|char|String)\\s+.+;\\s*$",
            ParsedLine::parseNonFinalVarDeclaration),

    /**
     * Matches method declarations, e.g., "void myMethod(int a) {".
     */
    METHOD_DECLARATION("^\\s*void\\s+[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*\\{\\s*$",
            ParsedLine::parseMethodDeclaration),

    /**
     * Matches if or while statements, e.g., "if (condition) {" or "while (condition) {".
     */
    IF_WHILE("^\\s*(if|while)\\s*\\(.*\\)\\s*\\{\\s*$",
            ParsedLine::parseIfWhile),

    /**
     * Matches return statements, e.g., "return;".
     */
    RETURN("^\\s*return\\s*;\\s*$",
            ParsedLine::parseReturn),

    /**
     * Matches closing brackets "}" possibly with leading/trailing whitespace.
     */
    CLOSING_BRACKET("^\\s*}\\s*$", line -> null),

    /**
     * Matches variable assignments, e.g., "x = 10;" or "a = 5, b = 6;".
     */
    VARIABLE_ASSIGNMENT("^\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+(\\s*,\\s*[a-zA-Z_]\\w*\\s*=\\s*[^,;]+)*\\s*;\\s*$",
            ParsedLine::parseVariableAssignment),

    /**
     * Matches method calls, e.g., "myMethod(5, "test");".
     */
    METHOD_CALL("^\\s*[a-zA-Z]\\w*\\s*\\([^)]*\\)\\s*;\\s*$",
            ParsedLine::parseMethodCall);


    // Regex pattern for matching lines of this type.
    private final Pattern pattern;
    // Parser function for strictly parsing lines of this type.
    private final StrictParser parser;


    /*
        * Constructor for LineType enum. Compiles the given regex pattern and assigns the parser function.
        * @param regex The regex pattern as a string.
        * @param parser The parser function to strictly parse lines of this type.
     */
    LineType(@Language("RegExp") String regex, StrictParser parser) {
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
     * @param line The line to parse.
     * @return A ParsedLine object representing the parsed line.
     * @throws IllegalArgumentException if the line does not conform to the expected format.
     */
    public ParsedLine parseStrict(String line) {
        return parser.parse(line);
    }

    /**
     * Classifies the given line into one of the defined LineType enums based on regex matching.
     * Lines must end with ';', '{', or '}' to be considered valid.
     * @param line The line to classify.
     * @return The corresponding LineType.
     * @throws IllegalArgumentException if the line does not match any known type or does not end with
     *                                  the required characters.
     */
    public static LineType classify(String line) {
        if (!(line.endsWith(";") || line.endsWith("{") || line.endsWith("}"))) {
            throw new IllegalArgumentException("Line must end with ';', '{', or '}' → " + line);
        }

        for (LineType type : values()) {
            if (type.pattern.matcher(line).matches()) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unrecognized line: " + line);
    }

    // Functional interface for strict parsing of lines.
    @FunctionalInterface
    private interface StrictParser {
        ParsedLine parse(String line);
    }

}



