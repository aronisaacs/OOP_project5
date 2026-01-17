package ex5.firstpass;

import ex5.lines.LineTypeFactory;
import ex5.lines.LineType;


import java.util.ArrayList;
import java.util.List;

/**
 * First pass parser that processes the code lines to build symbol and method tables.
 * Handles method declarations, variable declarations, and scope management.
 * Throws SyntaxException for syntax errors.
 *
 * @author Aron Isaacs
 * @see SyntaxException
 * @see ParsedLine
 * @see LineType
 */
public class FirstPassParser {

	private static final String UNMATCHED_BRACKETS_MESSAGE = "Unmatched brackets at end of file";
	private static final String NESTED_METHOD_MESSAGE = "Nested method declaration at line ";
	private static final String IFWHILE_OUTSIDE_MESSAGE = "if/while outside of method at line ";
	private static final String NOT_ALLOWED_GLOBAL_MESSAGE = " not allowed in global scope at line ";
	private static final String UNMATCHED_CLOSING_BRACKET_MESSAGE = "Unmatched closing bracket at line ";
	private final List<ParsedLine> globalLines;
	private final List<ParsedLine> methodSignatures;
	private final List<List<ParsedLine>> methodLines;
	private int currentScopeLevel = 0;


	/**
	 * Constructor for FirstPassParser.
	 *
	 * @param globalLines      list to store global lines
	 * @param methodSignatures method signature table to populate
	 * @param methodLines      list to store the lines of each method (a list of lists of ParsedLines)
	 */
	public FirstPassParser(List<ParsedLine> globalLines,
						   List<ParsedLine> methodSignatures,
						   List<List<ParsedLine>> methodLines) {
		this.globalLines = globalLines;
		this.methodSignatures = methodSignatures;
		this.methodLines = methodLines;

	}

	/**
	 * Parses the given list of code lines.
	 * Updates the symbol and method tables accordingly.
	 *
	 * @param codeLines list of code lines to parse
	 * @throws SyntaxException if a syntax error is encountered
	 */
	public void parse(List<String> codeLines) throws SyntaxException {
		int lineNumber = 0;
		// Iterate through each line, classify it, and handle it based on its type.
		for (String line : codeLines) {
			lineNumber++;
			LineType lineType = LineTypeFactory.classify(line, lineNumber);
			ParsedLine parsedLine = lineType.parseStrict(line, lineNumber);
			classifyLines(lineType, lineNumber, parsedLine);
		}
		if (currentScopeLevel != 0) {
			throw new SyntaxException(UNMATCHED_BRACKETS_MESSAGE);
		}
	}

	/**
	 * Handles the logic for different line types during parsing.
	 * Updates scope level and method lines as needed.
	 * @param type       the type of the line
	 * @param lineNumber the current line number
	 * @param parsedLine the parsed representation of the line
	 * @throws SyntaxException if a syntax error is encountered
	 */
	private void classifyLines(LineType type, int lineNumber, ParsedLine parsedLine) throws SyntaxException {
		switch (type) {
			case EMPTY:
			case COMMENT:
				break;

			case METHOD_DECLARATION:
				handleMethodDeclaration(parsedLine, lineNumber);
				break;

			case IF_WHILE:
				handleIfWhileEntry(parsedLine, lineNumber);
				break;

			case RETURN:
			case METHOD_CALL:
				handleMethodInternalLine(parsedLine, type, lineNumber);
				break;

			case CLOSING_BRACKET:
				handleClosingBracket(parsedLine, lineNumber);
				break;

			case FINAL_VAR_DECLARATION:
			case NON_FINAL_VAR_DECLARATION:
			case VARIABLE_ASSIGNMENT:
				handleVariableLine(parsedLine);
				break;
		}
	}

	/**
	 * Handles new method declarations and ensures they are not nested.
	 */
	private void handleMethodDeclaration(ParsedLine parsedLine, int lineNumber) throws SyntaxException {
		if (currentScopeLevel != 0) {
			throw new SyntaxException(NESTED_METHOD_MESSAGE + lineNumber);
		}
		currentScopeLevel++;
		methodSignatures.add(parsedLine);
		methodLines.add(new ArrayList<>());
		methodLines.getLast().add(parsedLine);
	}

	/**
	 * Handles entry into if/while blocks.
	 */
	private void handleIfWhileEntry(ParsedLine parsedLine, int lineNumber) throws SyntaxException {
		if (currentScopeLevel == 0) {
			throw new SyntaxException(IFWHILE_OUTSIDE_MESSAGE + lineNumber);
		}
		currentScopeLevel++;
		methodLines.getLast().add(parsedLine);
	}

	/**
	 * Handles code that is strictly prohibited in the global scope.
	 */
	private void handleMethodInternalLine(ParsedLine parsedLine, LineType type, int lineNumber)
			throws SyntaxException {
		if (currentScopeLevel == 0) {
			throw new SyntaxException(type + NOT_ALLOWED_GLOBAL_MESSAGE + lineNumber);
		}
		methodLines.getLast().add(parsedLine);
	}

	/**
	 * Handles closing brackets and updates the scope level.
	 */
	private void handleClosingBracket(ParsedLine parsedLine, int lineNumber) throws SyntaxException {
		if (currentScopeLevel == 0) {
			throw new SyntaxException(UNMATCHED_CLOSING_BRACKET_MESSAGE + lineNumber);
		}
		currentScopeLevel--;
		methodLines.getLast().add(parsedLine);
	}

	/**
	 * Handles variable declarations and assignments in both global and local scopes.
	 */
	private void handleVariableLine(ParsedLine parsedLine) {
		if (currentScopeLevel == 0) {
			globalLines.add(parsedLine);
		} else {
			methodLines.getLast().add(parsedLine);
		}
	}
}
