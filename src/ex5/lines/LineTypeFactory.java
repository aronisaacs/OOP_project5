package ex5.lines;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.SyntaxException;
import java.util.regex.Pattern;

/**
 * Factory class to classify lines into their respective LineType.
 * Uses regex patterns defined in LineType enum to match lines.
 * Throws SyntaxException for unrecognized lines.
 * @author Aron Isaacs
 * @see LineType
 * @see ParsedLine
 */
public class LineTypeFactory {

	private static final String LINE_LITERAL = "Line ";
	private static final String MUST_END_MESSAGE = " must end with ';', '{', or '}', got: ";
	private static final String UNRECOGNIZED_LINE_MESSAGE = "Unrecognized line ";
	private static final String COLON = ": ";
	private static final String SEMICOLON_LITERAL = ";";
	private static final String OPEN_BRACKET_LITERAL = "{";
	private static final String CLOSE_BRACKET_LITERAL = "}";

	/**
	 * Classifies a line into its corresponding LineType.
	 * Iterates through all LineType values and matches the line against their patterns.
	 * @param line The line to classify.
	 * @param lineNumber The line number in the source code (forerror reporting).
	 * @return The corresponding LineType.
	 * @throws SyntaxException if the line does not match any known LineType.
	 */
	public static LineType classify(String line, int lineNumber) throws SyntaxException {
		String trimmedLine = line.trim();
		for (LineType type : LineType.values()) {
			Pattern pattern = type.getPattern();
			if (pattern != null && pattern.matcher(trimmedLine).matches()) {
				if (type != LineType.COMMENT && type != LineType.EMPTY) {
					if (!(trimmedLine.endsWith(SEMICOLON_LITERAL) ||
							trimmedLine.endsWith(OPEN_BRACKET_LITERAL) ||
							trimmedLine.endsWith(CLOSE_BRACKET_LITERAL))) {
						throw new SyntaxException(LINE_LITERAL + lineNumber +
								MUST_END_MESSAGE + trimmedLine);
					}
				}
				return type;
			}
		}
		throw new SyntaxException(UNRECOGNIZED_LINE_MESSAGE + lineNumber + COLON + line);
	}
}


