package ex5.lines;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.SyntaxException;


import java.util.regex.Pattern;

/**
 * Factory class to classify lines into their respective LineType.
 * Uses regex patterns defined in LineType enum to match lines.
 * Throws SyntaxException for unrecognized lines.
 *
 * @author Aron Isaacs
 * @see LineType
 * @see ParsedLine
 */
public class LineTypeFactory {

	/**
	 * Classifies a line into its corresponding LineType.
	 * Iterates through all LineType values and matches the line against their patterns.
	 *
	 * @param line The line to classify.
	 * @return The corresponding LineType.
	 * @throws SyntaxException if the line does not match any known LineType.
	 */
	public static LineType classify(String line, int lineNumber) throws SyntaxException {
		String trimmedLine = line.trim();
		for (LineType type : LineType.values()) {
			Pattern pattern = type.getPattern();
			if (pattern != null && pattern.matcher(trimmedLine).matches()) {
				if (type != LineType.COMMENT && type != LineType.EMPTY) {
					if (!(trimmedLine.endsWith(";") || trimmedLine.endsWith("{") ||
							trimmedLine.endsWith("}"))) {
						throw new SyntaxException("Line " + lineNumber +
								" must end with ';', '{', or '}', got: " + trimmedLine);
					}
				}
				return type;
			}
		}
		;
		throw new SyntaxException("Unrecognized line " + lineNumber + ": " + line);
	}
}


