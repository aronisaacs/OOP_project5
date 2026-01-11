package ex5.firstpass;

import ex5.lines.LineType;
import ex5.firstpass.data.LineData;

public class ParsedLine {
	private final int lineNumber;
	private final String rawLine;
	private final LineType type;
	private final LineData data; // Placeholder for additional parsed data

	//constructor
	public ParsedLine(int lineNumber, String rawLine, LineType type, LineData data) {
		this.lineNumber = lineNumber;
		this.rawLine = rawLine;
		this.type = type;
		this.data = data;
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
