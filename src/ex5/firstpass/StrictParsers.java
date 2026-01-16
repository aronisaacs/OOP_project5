package ex5.firstpass;

import ex5.lines.LineType;
import ex5.firstpass.data.MethodDeclarationData;
import ex5.firstpass.data.MethodCallData;
import ex5.firstpass.data.ConditionData;
import ex5.firstpass.data.VarDeclarationData;
import ex5.firstpass.data.VarAssignData;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing strict parsing methods
 * for different line types in the simplified Java-like language.
 * Each method validates the syntax and extracts relevant data.
 * @author ron.stein
 */
public final class StrictParsers {
	//REGEX PATTERNS
	private static final Pattern METHOD_NAME_PATTERN =
			Pattern.compile("^[A-Za-z]\\w*$");
	private static final String S_JAVA_IDENT = "(_[A-Za-z0-9]|[A-Za-z])\\w*";
	private static final Pattern PARAM_PATTERN =
			Pattern.compile("^\\s*(final\\s+)?(\\w+)\\s+(" + S_JAVA_IDENT + ")\\s*$");
	private static final Pattern IDENT_PATTERN =
			Pattern.compile("^" + S_JAVA_IDENT + "$");

	//CONSTANT REGEX STRINGS
	private static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
	private static final String WHITESPACE_REGEX = "\\s*,\\s*";

	//CONSTANT MESSAGES
	private static final String INVALID_COMMENT_MESSAGE = "Invalid comment syntax at line ";
	private static final String MISSING_VARIABLE_MESSAGE = "Missing type/variables at line ";
	private static final String MISSING_VARIABLE_NAME_MESSAGE = "Missing variable names at line ";
	private static final String FINAL_VARIABLE_MESSAGE = "Final variable must be initialized at line ";
	private static final String EMPTY_DECLERATION_MESSAGE = "Empty declaration item at line ";

	//CONSTANT STRING LITERALS
	private static final String FINAL_LITERAL = "final";
	private static final String COMMENT_LITERAL = "//";

	private StrictParsers() {}

	/**
	 * Parses a final variable declaration line.
	 * Extracts the type and variable items (name and optional initializer).
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the final variable declaration with declaration data
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseFinalVarDeclaration(String line, int lineNumber)
			throws SyntaxException {

		String noSemi = stripTrailingSemicolon(line);
		String afterFinal = stripLeadingKeyword(noSemi, FINAL_LITERAL);

		TypeAndRest tr = splitTypeRest(afterFinal, lineNumber);
		List<VarDeclarationData.Item> items = parseVarItems(tr.getRest(), lineNumber, true);

		VarDeclarationData data = new VarDeclarationData(true, tr.getType(), items);
		return new ParsedLine(lineNumber, line, LineType.FINAL_VAR_DECLARATION, data);
	}

	/**
	 * Strips the trailing semicolon from a line.
	 * @param line raw line string
	 * @return the line without the trailing semicolon
	 */
	private static String stripTrailingSemicolon(String line) {
		String t = line.trim();
		return t.substring(0, t.length() - 1).trim(); // enum guarantees ';'
	}

	/**
	 * Strips the leading keyword from a line.
	 * @param s raw line string
	 * @param keyword the leading keyword to strip
	 * @return the line without the leading keyword
	 */
	private static String stripLeadingKeyword(String s, String keyword) {
		return s.substring(keyword.length()).trim(); // enum guarantees prefix
	}

	/**
	 * Parses a comment line.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return ParsedLine object representing the comment line
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseComment(String line, int lineNumber) throws SyntaxException {
		if(!line.startsWith(COMMENT_LITERAL)){
			throw new SyntaxException(INVALID_COMMENT_MESSAGE + lineNumber);
		}
		return new ParsedLine(lineNumber, line, LineType.COMMENT, null);
	}

	private static final class TypeAndRest {
		private final PrimitiveType type;
		private final String rest;

		private TypeAndRest(PrimitiveType type, String rest) {
			this.type = type;
			this.rest = rest;
		}

		/**
		 * Gets the primitive type.
		 * @return the primitive type
		 */
		public PrimitiveType getType() {
			return type;
		}

		/**
		 * Gets the rest of the line after the type.
		 * @return the rest of the line
		 */
		public String getRest() {
			return rest;
		}
	}

	/**
	 * Splits the type and the rest of the line.
	 * @param s the line string
	 * @param lineNumber line number in the source file
	 * @return a TypeAndRest object containing the primitive type and the rest of the line
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	private static TypeAndRest splitTypeRest(String s, int lineNumber)
			throws SyntaxException {

		int firstSpace = s.indexOf(' ');
		if (firstSpace < 0) {
			throw new SyntaxException(MISSING_VARIABLE_MESSAGE + lineNumber);
		}
		String typeToken = s.substring(0, firstSpace).trim();
		PrimitiveType type = PrimitiveType.fromTypeName(typeToken);

		String rest = s.substring(firstSpace + 1).trim();
		if (rest.isEmpty()) {
			throw new SyntaxException(MISSING_VARIABLE_NAME_MESSAGE + lineNumber);
		}
		return new TypeAndRest(type, rest);
	}

	/**
	 * Parses variable declaration items from the rest of the line.
	 * @param rest the rest of the line after the type
	 * @param lineNumber line number in the source file
	 * @param isFinal indicates if the variable is final
	 * @return a list of VarDeclarationData.Item objects representing the variable declarations
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	private static List<VarDeclarationData.Item> parseVarItems(
			String rest, int lineNumber, boolean isFinal)
			throws SyntaxException {

		String[] parts = rest.split(WHITESPACE_REGEX);
		List<VarDeclarationData.Item> items = new ArrayList<>();

		for (String part : parts) {
			VarDeclarationData.Item item = parseSingleDeclItem(part, lineNumber);

			if (isFinal && item.getValueToken() == null) {
				throw new SyntaxException(
						FINAL_VARIABLE_MESSAGE + lineNumber);
			}

			items.add(item);
		}
		return items;
	}

	/**
	 * Parses a single variable declaration item.
	 * @param part the declaration item string
	 * @param lineNumber line number in the source file
	 * @return a VarDeclarationData.Item object representing the variable declaration
	 * @throws SyntaxException if the item does not conform to the expected format
	 */
	private static VarDeclarationData.Item parseSingleDeclItem(String part, int lineNumber)
			throws SyntaxException {

		String p = part.trim();
		if (p.isEmpty()) {
			throw new SyntaxException(EMPTY_DECLERATION_MESSAGE + lineNumber);
		}

		String[] eqParts = p.split("\\s*=\\s*", -1);
		if (eqParts.length > 2) {
			throw new SyntaxException("Multiple '=' in declaration at line " + lineNumber);
		}

		String name = eqParts[0].trim();
		if (!IDENT_PATTERN.matcher(name).matches()) {
			throw new SyntaxException("Invalid variable name at line " + lineNumber);
		}

		String valueToken = null;
		if (eqParts.length == 2) {
			valueToken = eqParts[1].trim();
			if (valueToken.isEmpty()) {
				throw new SyntaxException("Missing initializer at line " + lineNumber);
			}
		}
		return new VarDeclarationData.Item(name, valueToken);
	}

	/**
	 * Parses a non-final variable declaration line.
	 * Extracts the type and variable items (name and optional initializer).
	 * uses similar logic to final variable parsing but without the "final" keyword.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the non-final variable declaration with declaration data
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseNonFinalVarDeclaration(String line, int lineNumber)
			throws SyntaxException {

		String noSemi = stripTrailingSemicolon(line);

		TypeAndRest tr = splitTypeRest(noSemi, lineNumber); // validates PrimitiveType using fromTypeName
		List<VarDeclarationData.Item> items = parseVarItems(tr.getRest(), lineNumber, false);

		VarDeclarationData data = new VarDeclarationData(false, tr.getType(), items);
		return new ParsedLine(lineNumber, line, LineType.NON_FINAL_VAR_DECLARATION, data);
	}

	/**
	 * Parses a method declaration line.
	 * Extracts the method name and parameters,
	 * and saves them in a MethodDeclarationData object.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the method declaration with method data
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseMethodDeclaration(String line, int lineNumber)
			throws SyntaxException {

		String trimmed = line.trim();
		// remove leading "void" and ending '{'
		String afterVoid = trimmed.substring("void".length(), trimmed.length() - 1).trim();

		int openParen = afterVoid.indexOf('(');
		int closeParen = afterVoid.lastIndexOf(')');

		String methodName = afterVoid.substring(0, openParen).trim();
		String paramsSection = afterVoid.substring(openParen + 1, closeParen).trim();

		List<MethodDeclarationData.ParamInfo> params = new ArrayList<>();

		if (!paramsSection.isEmpty()) {
			String[] parts = paramsSection.split("\\s*,\\s*");

			for (String part : parts) {
				Matcher pm = PARAM_PATTERN.matcher(part);
				if (!pm.matches()) {
					throw new SyntaxException(
							"Invalid parameter syntax at line " + lineNumber);
				}

				boolean isFinal = (pm.group(1) != null);
				String typeToken = pm.group(2);
				String paramName = pm.group(3);

				PrimitiveType type = PrimitiveType.fromTypeName(typeToken);

				params.add(new MethodDeclarationData.ParamInfo(isFinal, type, paramName));
			}
		}

		MethodDeclarationData data = new MethodDeclarationData(methodName, params);
		return new ParsedLine(lineNumber, line, LineType.METHOD_DECLARATION, data);
	}


	/**
	 * Parses an if or while line.
	 * Extracts the condition, splits it into operands and operators,
	 * and saves them in a ConditionData object.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the if/while statement with condition data
	 * @throws SyntaxException if the line does not conform to the expected format
	 */ //Method is too long
	public static ParsedLine parseIfWhile(String line, int lineNumber)
			throws SyntaxException {

		String trimmed = line.trim();
		boolean isWhile = trimmed.startsWith("while");

		String withoutBrace = trimmed.substring(0, trimmed.length() - 1).trim();

		int openParen = withoutBrace.indexOf('(');
		int closeParen = withoutBrace.lastIndexOf(')');

		String condition = withoutBrace.substring(openParen + 1, closeParen).trim();
		if (condition.isEmpty()) {
			throw new SyntaxException("Empty condition at line " + lineNumber);
		}

		List<String> operands = new ArrayList<>();
		List<String> operators = new ArrayList<>();

		int i = 0;
		StringBuilder current = new StringBuilder();

		while (i < condition.length()) {
			char c = condition.charAt(i);

			if (c == '&' || c == '|') {
				// must be && or ||
				if (i + 1 >= condition.length() || condition.charAt(i + 1) != c) {
					throw new SyntaxException("Invalid operator in condition at line " + lineNumber);
				}

				String operand = current.toString().trim();
				if (operand.isEmpty()) {
					throw new SyntaxException("Empty operand in condition at line " + lineNumber);
				}
				operands.add(operand);
				current.setLength(0);

				operators.add("" + c + c);
				i += 2;
				continue;
			}

			current.append(c);
			i++;
		}

		String lastOperand = current.toString().trim();
		if (lastOperand.isEmpty()) {
			throw new SyntaxException("Empty operand in condition at line " + lineNumber);
		}
		operands.add(lastOperand);

		ConditionData data = new ConditionData(isWhile, operands, operators);
		return new ParsedLine(lineNumber, line, LineType.IF_WHILE, data);
	}

	/**
	 * Parses a return statement line.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the return statement
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseReturn(String line, int lineNumber)
			throws SyntaxException{
		if(!line.matches(RETURN_REGEX)) {
			throw new SyntaxException("Invalid return statement at line " + lineNumber);
		}
		return new ParsedLine(lineNumber, line, LineType.RETURN, null);
	}

	/**
	 * Parses a closing bracket line.
	 * @param line the raw line string
	 * @param lineNumber the line number in the source file
	 * @return a ParsedLine object representing the closing bracket
	 */
	public static ParsedLine parseClosingBracket(String line, int lineNumber) {
		return new ParsedLine(lineNumber, line, LineType.CLOSING_BRACKET, null);
	}

	/**
	 * Parses a variable assignment line.
	 * Extracts variable names and assigned values,
	 * and saves them in a VarAssignData object.
	 * @param line raw line string
	 * @param lineNumber line number in the source file
	 * @return a ParsedLine object representing the variable assignment with assignment data
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseVariableAssignment(String line, int lineNumber)
			throws SyntaxException {

		String noSemi = stripTrailingSemicolon(line);

		String[] parts = noSemi.split("\\s*,\\s*");
		List<VarAssignData.Item> items = new ArrayList<>();

		for (String part : parts) {
			String p = part.trim();
			if (p.isEmpty()) {
				throw new SyntaxException("Empty assignment item at line " + lineNumber);
			}

			String[] eqParts = p.split("\\s*=\\s*", -1);
			if (eqParts.length != 2) {
				throw new SyntaxException("Invalid assignment syntax at line " + lineNumber);
			}

			String name = eqParts[0].trim();
			String valueToken = eqParts[1].trim();

			if (!IDENT_PATTERN.matcher(name).matches()) {
				throw new SyntaxException("Invalid variable name at line " + lineNumber);
			}
			if (valueToken.isEmpty()) {
				throw new SyntaxException("Missing assigned value at line " + lineNumber);
			}

			items.add(new VarAssignData.Item(name, valueToken));
		}

		VarAssignData data = new VarAssignData(items);
		return new ParsedLine(lineNumber, line, LineType.VARIABLE_ASSIGNMENT, data);
	}


	/**
	 * Parses a method call line.
	 * saves the method name and the arguments in a MethodCallData object.
	 * @param line the raw line string
	 * @param lineNumber the line number in the source file
	 * @return a ParsedLine object representing the method call
	 * @throws SyntaxException if the line does not conform to the expected format
	 */
	public static ParsedLine parseMethodCall(String line, int lineNumber)
			throws SyntaxException {

		String withoutSemi = line.trim();
		withoutSemi = withoutSemi.substring(0, withoutSemi.length() - 1).trim();

		int openParen = withoutSemi.indexOf('(');
		int closeParen = withoutSemi.lastIndexOf(')');

		String methodName = withoutSemi.substring(0, openParen).trim();
		String argsSection = withoutSemi.substring(openParen + 1, closeParen).trim();

		if (!METHOD_NAME_PATTERN.matcher(methodName).matches()) {
			throw new SyntaxException("Invalid method name in call at line " + lineNumber);
		}

		List<String> args = new ArrayList<>();
		if (!argsSection.isEmpty()) {
			String[] parts = argsSection.split("\\s*,\\s*");
			for (String part : parts) {
				String arg = part.trim();
				if (arg.isEmpty()) {
					throw new SyntaxException("Empty argument in method call at line " + lineNumber);
				}
				args.add(arg);
			}
		}

		MethodCallData data = new MethodCallData(methodName, args);
		return new ParsedLine(lineNumber, line, LineType.METHOD_CALL, data);
	}



}
