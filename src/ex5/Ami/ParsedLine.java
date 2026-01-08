package ex5.Ami;

import ex5.lines.LineType;
import ex5.parser.SJavaParseException;

public class ParsedLine {
	private final int lineNumber;
	private final String rawLine;
	private final LineType type;
	private LineData data; // Placeholder for additional parsed data

	public ParsedLine(int lineNumber, String rawLine, LineType type, LineData data) {
		this.lineNumber = lineNumber;
		this.rawLine = rawLine;
		this.type = type;
		this.data = data;
	}



	/**
	 * Marker interface for line-specific parsed data.
	 * each line type can implement this to hold its specific data.
	 */
	public interface LineData {}

	public static final class EmptyData implements LineData {}
	public static final class CommentData implements LineData {}
	public static final class CloseBraceData implements LineData {}


	// Static parsing methods for different line types
	public static ParsedLine parseFinalVarDeclaration(String line, int lineNumber)
			throws SJavaParseException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static ParsedLine parseNonFinalVarDeclaration(String line, int lineNumber)
			throws SJavaParseException{
        throw new UnsupportedOperationException("Not implemented");
    }

    public static ParsedLine parseMethodDeclaration(String line, int lineNumber)
			throws SJavaParseException{
        throw new UnsupportedOperationException("Not implemented");
    }

    public static ParsedLine parseIfWhile(String line, int lineNumber)
			throws SJavaParseException{
        throw new UnsupportedOperationException("Not implemented");
    }

    public static ParsedLine parseReturn(String line, int lineNumber)
			throws SJavaParseException{
		String trimmedLine = line.trim();
		if(!trimmedLine.equals("return;")) {
			throw new SJavaParseException("Invalid return statement at line " + lineNumber);
		}
		return new ParsedLine(lineNumber, line, LineType.RETURN, null);
    }
	public static ParsedLine parseClosingBracket(String line, int lineNumber) {
		return new ParsedLine(lineNumber, line, LineType.CLOSING_BRACKET, null);
	}

    public static ParsedLine parseVariableAssignment(String line, int lineNumber)
			throws SJavaParseException{
        throw new UnsupportedOperationException("Not implemented");
    }

    public static ParsedLine parseMethodCall(String line, int lineNumber)
			throws SJavaParseException{
        throw new UnsupportedOperationException("Not implemented");
    }

	// Getters for the fields
	/**
	 * Gets the line number of this parsed line.
	 * @return The line number.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the raw line string of this parsed line.
	 * @return The raw line string.
	 */
	public String getRawLine() {
		return rawLine;
	}

	/**
	 * Gets the type of this parsed line.
	 * @return The LineType of this parsed line.
	 */
	public LineType getType() {
		return type;
	}

	/**
	 * Gets the parsed data of this line.
	 * @return The LineData associated with this parsed line.
	 */
	public LineData getData() {
		return data;
	}


}
