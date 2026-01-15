package ex5.lines;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.SyntaxException;


import java.util.regex.Pattern;

/**
 * Factory class to classify lines into their respective LineType.
 * Uses regex patterns defined in LineType enum to match lines.
 * Throws SyntaxException for unrecognized lines.
 * @see LineType
 * @see ParsedLine
 * @author Aron Isaacs
 */
public class LineTypeFactory {

    /**
     * Classifies a line into its corresponding LineType.
     * Iterates through all LineType values and matches the line against their patterns.
     * @throws SyntaxException if the line does not match any known LineType.
     * @param line The line to classify.
     * @return The corresponding LineType.
     */
    public static LineType classify(String line) throws SyntaxException {
		line = line.trim();
        for (LineType type : LineType.values()) {
            Pattern pattern = type.getPattern();
            if (pattern != null && pattern.matcher(line).matches()) {
				if (type != LineType.COMMENT && type != LineType.EMPTY){
					if (!(line.endsWith(";") || line.endsWith("{") || line.endsWith("}"))) {
						throw new SyntaxException("Line must end with ';', '{', or '}', got: " + line);
					}
				}
                return type;
            }
        };
        throw new SyntaxException("Unrecognized line: " + line);
    }
}


