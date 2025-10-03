package ex5.lines;

import ex5.Ami.ParsedLine;
import java.util.regex.Pattern;

/**
 * Factory class to classify lines into their respective LineType.
 * Uses regex patterns defined in LineType enum to match lines.
 * Throws IllegalArgumentException for unrecognized lines.
 * @see LineType
 * @see ParsedLine
 * @author Aron Isaacs
 */
public class LineTypeFactory {

    /**
     * Classifies a line into its corresponding LineType.
     * Iterates through all LineType values and matches the line against their patterns.
     * @throws IllegalArgumentException if the line does not match any known LineType.
     * @param line The line to classify.
     * @return The corresponding LineType.
     */
    public static LineType classify(String line) {
        for (LineType type : LineType.values()) {
            Pattern pattern = type.getPattern();
            if (pattern != null && pattern.matcher(line).matches()) {
                return type;
            }
        };
        throw new IllegalArgumentException("Unrecognized line: " + line);
    }
}


