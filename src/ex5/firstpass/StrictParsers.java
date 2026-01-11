package ex5.firstpass;

import ex5.firstpass.data.MethodDeclarationData;
import ex5.lines.LineType;
import ex5.parser.SJavaParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StrictParsers {
	private static final Pattern METHOD_NAME_PATTERN =
			Pattern.compile("^[A-Za-z]\\w*$");
	private static final Pattern PARAM_PATTERN =
			Pattern.compile("^\\s*(final\\s+)?(\\w+)\\s+([A-Za-z]\\w*)\\s*$");

	private StrictParsers() {}

	public static ParsedLine parseFinalVarDeclaration(String line, int lineNumber)
			throws SJavaParseException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public static ParsedLine parseNonFinalVarDeclaration(String line, int lineNumber)
			throws SJavaParseException{
		throw new UnsupportedOperationException("Not implemented");
	}

	public static ParsedLine parseMethodDeclaration(String line, int lineNumber)
			throws SJavaParseException {

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
					throw new SJavaParseException(
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

}
