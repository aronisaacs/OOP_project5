package ex5.secondpass;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.PrimitiveType;
import ex5.firstpass.data.*;
import ex5.lines.LineType;

import java.util.*;

/**
 * SecondPassParser processes ParsedLines to validate method signatures and check global and method lines.
 *
 * @author ron.stein
 * @see SemanticException
 * @see ParsedLine
 */
public class SecondPassParser {

	private static final String INVALID_METHOD_SIGNATURE_MESSAGE = "Invalid method signature at line ";
	private static final String DUPLICATE_METHOD_SIGNATURE = "Duplicate method name '";
	private static final String AT_LINE_MESSAGE = "' at line ";
	private static final String DUPLICATE_PARAMETER_MESSAGE = "Duplicate parameter name: ";
	private static final String METHOD_MESSAGE = "Method ";
	private static final String END_RETURN_MESSAGE = " must end with 'return;'";
	private static final String DUPLICATE_GLOBAL_MESSAGE = "Duplicate global variable name '";
	private static final String FINAL_VARIABLE_MESSAGE = "Final variable '";
	private static final String NOT_INITIALIZED_METHOD = "' not initialized";
	private static final String UNDECLARED_GLOBAL_VARIABLE_MESSAGE = "Undeclared global variable: ";
	private static final String FINAL_GLOBAL_VALUE_MESSAGE =
			"Cannot assign a value to final global variable: ";
	private static final String VARIABLE_USED_MESSAGE = "Variable used before initialization: ";
	private static final String TYPE_MISMATCH_MESSAGE = "Type mismatch: cannot assign ";
	private static final String TO = " to ";
	private static final String INVALID_MESSAGE = "Invalid literal '";
	private static final String FOR_TYPE_MESSAGE = "' for type ";
	private static final String UNEXPECTED_CLOSING_BRACKET_MESSAGE = "Unexpected closing bracket.";
	private static final String VARIABLE_DEFINED_IN_SCOPE = "Variable already defined in this scope: ";
	private static final String FINAL_MUST_BE_INITIALIZED_MESSAGE =
			"Final local variable must be initialized.";
	private static final String UNDECLARED_VARIABLE_MESSAGE = "Undeclared variable: ";
	private static final String REASSIGN_FINAL_VARIABLE_MESSAGE = "Cannot reassign a final variable.";
	private static final String METHOD_NOT_FOUND_MESSAGE = "Method not found: ";
	private static final String ARGUMENT_COUNT_MESSAGE = "Argument count mismatch for ";
	private static final String QUOTES_LITERAL = "'";
	private static final int THREE_LITERAL = 3;
	private static final String QUESTIONMARK_LITERAL = "?";
	private final List<ParsedLine> globalLines;
	private final List<ParsedLine> methodSignatures;
	private final List<List<ParsedLine>> methodLines;
	private final Map<String, MethodSymbol> methodTable = new HashMap<>();
	private final Deque<Map<String, VariableSymbol>> scopeStack = new ArrayDeque<>();

	private static final String INT_REGEX = "^[+-]?\\d+$";
	private static final String DOUBLE_REGEX = "^[+-]?(\\d+|\\d+\\.|\\.\\d+)(\\d+)?$";
	private static final String STRING_REGEX = "^\".*\"$";
	private static final String CHAR_REGEX = "^'.'$";
	private static final String BOOLEAN_REGEX = "^(true|false)$";

	/**
	 * Constructor for SecondPassParser.
	 *
	 * @param globalLines      List of global ParsedLines.
	 * @param methodSignatures List of method signature ParsedLines.
	 * @param methodLines      List of lists of ParsedLines for each method.
	 */
	public SecondPassParser(List<ParsedLine> globalLines, List<ParsedLine> methodSignatures,
							List<List<ParsedLine>> methodLines) {
		this.globalLines = globalLines;
		this.methodSignatures = methodSignatures;
		this.methodLines = methodLines;
	}

	/**
	 * Parses the stored ParsedLines.
	 * @throws SemanticException if a semantic error is encountered.
	 */
	public void parse() throws SemanticException {
		updateMethodSignatures();
		checkGlobalLines();
		checkMethodLines();
	}

	/**
	 * Updates method signatures.
	 */
	private void updateMethodSignatures() throws SemanticException {
		for (ParsedLine method : methodSignatures) {
			addMethodSignature(method);
		}
	}

	/**
	 * Adds a method signature to the method table.
	 */
	private void addMethodSignature(ParsedLine methodSignatureLine) throws SemanticException {
		//check signature line
		if (methodSignatureLine == null || methodSignatureLine.getData() == null) {
			throw new SemanticException(
					INVALID_METHOD_SIGNATURE_MESSAGE +
							(methodSignatureLine == null ? QUESTIONMARK_LITERAL :
									methodSignatureLine.getLineNumber()));
		}
		MethodDeclarationData data = (MethodDeclarationData) methodSignatureLine.getData();
		String methodName = data.getMethodName();

		//Duplicate method name check
		if (methodTable.containsKey(methodName)) {
			throw new SemanticException(
					DUPLICATE_METHOD_SIGNATURE + methodName +
							AT_LINE_MESSAGE + methodSignatureLine.getLineNumber());
		}
		List<MethodSymbol.Param> params = new ArrayList<>();
		for (MethodDeclarationData.ParamInfo p : data.getParams()) {
			params.add(new MethodSymbol.Param(
					p.getType(),
					p.isFinal(),
					p.getName()));
		}
		methodTable.put(methodName, new MethodSymbol(methodName, params));
	}


	/**
	 * Checks global lines.
	 */
	private void checkGlobalLines() throws SemanticException {
		Map<String, VariableSymbol> globalScope = new HashMap<>();
		scopeStack.push(globalScope);
		for (ParsedLine line : globalLines) {
			addGlobalLine(line);
		}
	}

	/**
	 * Checks method lines.
	 */
	private void checkMethodLines() throws SemanticException {
		for (List<ParsedLine> method : methodLines) {
			//Only the global scope should be on the stack
			while (scopeStack.size() > 1) {
				scopeStack.pop();
			}
			Map<String, VariableSymbol> methodScope = new HashMap<>();
			scopeStack.push(methodScope);

			ParsedLine signatureLine = method.getFirst();
			MethodDeclarationData data = (MethodDeclarationData) signatureLine.getData();
			for (MethodDeclarationData.ParamInfo p : data.getParams()) {
				// Parameters are local variables initialized by the caller.
				if (methodScope.containsKey(p.getName())) {
					throw new SemanticException(DUPLICATE_PARAMETER_MESSAGE + p.getName());
				}
				methodScope.put(p.getName(), new VariableSymbol(p.getType(), p.isFinal(), true));
			}

			for (int i = 1; i < method.size(); i++) {
				checkMethodLine(method.get(i));
			}
			// the last line in the list is the CLOSING_BRACKET.
			// The line before it must be a RETURN.
			if (method.size() < THREE_LITERAL || method.get(method.size() - 2).getType() != LineType.RETURN) {
				throw new SemanticException(METHOD_MESSAGE + data.getMethodName() + END_RETURN_MESSAGE);
			}
		}
	}

	/**
	 * Adds a global line.
	 */
	private void addGlobalLine(ParsedLine globalLine) {
		if (globalLine == null) return;
		LineType type = globalLine.getType();
		switch (type) {
			case FINAL_VAR_DECLARATION:
			case NON_FINAL_VAR_DECLARATION:
				checkGlobalDeclaration((VarDeclarationData) globalLine.getData());
				break;
			case VARIABLE_ASSIGNMENT:
				checkGlobalAssignment((VarAssignData) globalLine.getData());
				break;
			default:
				break;
		}
	}

	/**
	 * Checks global declaration lines.
	 */
	private void checkGlobalDeclaration(VarDeclarationData data) throws SemanticException {
		// The global scope is always the bottom-most map in our stack
		Map<String, VariableSymbol> globalScope = scopeStack.peek();

		for (VarDeclarationData.Item i : data.getItems()) {
			String name = i.getName();

			// 1. Check for duplicate global variable names
			assert globalScope != null;
			if (globalScope.containsKey(name)) {
				throw new SemanticException(DUPLICATE_GLOBAL_MESSAGE + name + QUOTES_LITERAL);
			}

			boolean isInitialized = false;
			String valueToken = i.getValueToken();

			if (valueToken != null) {
				validateValue(data.getType(), valueToken);
				isInitialized = true;
			} else if (data.isFinal()) {
				throw new SemanticException(FINAL_VARIABLE_MESSAGE + name + NOT_INITIALIZED_METHOD);
			}

			// 3. Register the variable in the global scope map
			globalScope.put(name, new VariableSymbol(data.getType(), data.isFinal(), isInitialized));
		}
	}

	/**
	 * Checks global assignment lines.
	 */
	private void checkGlobalAssignment(VarAssignData data) throws SemanticException {
		for (VarAssignData.Item i : data.getItems()) {
			String name = i.getName();

			// Use resolve to find the variable in the current stack
			VariableSymbol symbol = resolve(name);

			if (symbol == null) {
				throw new SemanticException(UNDECLARED_GLOBAL_VARIABLE_MESSAGE + name);
			}
			if (symbol.isFinal()) {
				throw new SemanticException(FINAL_GLOBAL_VALUE_MESSAGE + name);
			}
			validateValue(symbol.getType(), i.getValueToken());
			symbol.setInitialized(true);
		}
	}

	/**
	 * Validates a value token against a target type.
	 */
	private void validateValue(PrimitiveType targetType, String token) throws SemanticException {
		VariableSymbol sourceVar = resolve(token);
		if (sourceVar != null) {
			if (!sourceVar.isInitialized()) {
				throw new SemanticException(VARIABLE_USED_MESSAGE + token);
			}
			if (!isTypeCompatible(targetType, sourceVar.getType())) {
				throw new SemanticException(
						TYPE_MISMATCH_MESSAGE + sourceVar.getType() + TO + targetType);
			}
			return;
		}

		//If not a variable, check if it's a compatible literal
		if (!isLiteralCompatible(targetType, token)) {
			throw new SemanticException(INVALID_MESSAGE + token + FOR_TYPE_MESSAGE + targetType);
		}
	}

	/**
	 * Resolves a variable name to its VariableSymbol in the current scope stack.
	 */
	private VariableSymbol resolve(String name) {
		for (Map<String, VariableSymbol> scope : scopeStack) {
			if (scope.containsKey(name)) {
				return scope.get(name);
			}
		}
		return null;
	}

	/**
	 * Checks if source type can be assigned to target type.
	 */
	private boolean isTypeCompatible(PrimitiveType target, PrimitiveType source) {
		if (target == source) return true; // Direct match

		if (target == PrimitiveType.DOUBLE && source == PrimitiveType.INT) {
			return true;
		}
		if (target == PrimitiveType.BOOLEAN) {
			return source == PrimitiveType.INT || source == PrimitiveType.DOUBLE;
		}
		return false;
	}

	/**
	 * Checks if a literal token is compatible with the target type.
	 */
	private boolean isLiteralCompatible(PrimitiveType target, String token) {
		switch (target) {
			case INT:
				return token.matches(INT_REGEX);
			case DOUBLE:
				// double accepts both double and int literals
				return token.matches(DOUBLE_REGEX) || token.matches(INT_REGEX);
			case STRING:
				return token.matches(STRING_REGEX);
			case CHAR:
				return token.matches(CHAR_REGEX);
			case BOOLEAN:
				// boolean accepts true, false, or any numeric literal
				return token.matches(BOOLEAN_REGEX) ||
						token.matches(DOUBLE_REGEX) ||
						token.matches(INT_REGEX);
			default:
				return false;
		}
	}

	/**
	 * Checks a method line.
	 */
	private void checkMethodLine(ParsedLine methodLine) throws SemanticException {
		if (methodLine == null) return;
		LineType type = methodLine.getType();

		switch (type) {
			case FINAL_VAR_DECLARATION:
			case NON_FINAL_VAR_DECLARATION:
				handleLocalDeclaration((VarDeclarationData) methodLine.getData());
				break;

			case VARIABLE_ASSIGNMENT:
				handleLocalAssignment((VarAssignData) methodLine.getData());
				break;

			case IF_WHILE:
				handleIfWhile((ConditionData) methodLine.getData());
				break;

			case METHOD_CALL:
				handleMethodCall((MethodCallData) methodLine.getData());
				break;
			case CLOSING_BRACKET:
				// Pop the most recent scope
				if (scopeStack.size() <= 1) {
					throw new SemanticException(UNEXPECTED_CLOSING_BRACKET_MESSAGE);
				}
				scopeStack.pop();
				break;
			case RETURN:
				break;
			case METHOD_DECLARATION:
				break;
			default:
				break;
		}
	}

	/**
	 * Handles local variable declarations.
	 */
	private void handleLocalDeclaration(VarDeclarationData data) throws SemanticException {
		Map<String, VariableSymbol> currentScope = scopeStack.peek();
		for (VarDeclarationData.Item item : data.getItems()) {
			String name = item.getName();
			//Two local variables with same name cannot be in the same block
			assert currentScope != null;
			if (currentScope.containsKey(name)) {
				throw new SemanticException(VARIABLE_DEFINED_IN_SCOPE + name);
			}

			boolean initialized = false;
			if (item.getValueToken() != null) {
				validateValue(data.getType(), item.getValueToken());
				initialized = true;
			} else if (data.isFinal()) {
				throw new SemanticException(FINAL_MUST_BE_INITIALIZED_MESSAGE);
			}
			currentScope.put(name, new VariableSymbol(data.getType(), data.isFinal(), initialized));
		}
	}

	/**
	 * Handles local variable assignments.
	 */
	private void handleLocalAssignment(VarAssignData data) throws SemanticException {
		Map<String, VariableSymbol> currentScope = scopeStack.peek();
		for (VarAssignData.Item item : data.getItems()) {
			VariableSymbol symbol = resolve(item.getName());
			if (symbol == null) {
				throw new SemanticException(UNDECLARED_VARIABLE_MESSAGE + item.getName());
			}
			if (symbol.isFinal()) {
				throw new SemanticException(REASSIGN_FINAL_VARIABLE_MESSAGE);
			}
			validateValue(symbol.getType(), item.getValueToken());
			//we need to create a local version so we don't mutate the global one
			VariableSymbol localUpdate = new
					VariableSymbol(symbol.getType(), symbol.isFinal(), true);
			assert currentScope != null;
			currentScope.put(item.getName(), localUpdate);
		}
	}

	/**
	 * Handles if/while conditions.
	 */
	private void handleIfWhile(ConditionData data) throws SemanticException {
		//Conditions must be boolean compatible
		for (String operand : data.getOperands()) {
			validateValue(PrimitiveType.BOOLEAN, operand);
		}
		scopeStack.push(new HashMap<>());
	}

	/**
	 * Handles method calls.
	 */
	private void handleMethodCall(MethodCallData data) throws SemanticException {
		MethodSymbol target = methodTable.get(data.getMethodName());
		if (target == null) {
			throw new SemanticException(METHOD_NOT_FOUND_MESSAGE + data.getMethodName());
		}

		List<String> args = data.getArgs();
		List<MethodSymbol.Param> params = target.getParams();

		if (args.size() != params.size()) {
			throw new SemanticException(ARGUMENT_COUNT_MESSAGE + target.getName());
		}

		for (int i = 0; i < args.size(); i++) {
			//Argument types must match parameter types
			validateValue(params.get(i).getType(), args.get(i));
		}
	}


}

