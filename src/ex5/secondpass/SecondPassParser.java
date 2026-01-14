package ex5.secondpass;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.PrimitiveType;
import ex5.firstpass.data.*;
import ex5.lines.LineType;
import ex5.parser.SJavaParseException;

import java.util.*;

/**
 * SecondPassParser processes ParsedLines to validate method signatures and check global and method lines.
 * @see SJavaParseException
 * @see ParsedLine
 * @author ron.stein
 */
public class SecondPassParser {

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
	 * @param globalLines List of global ParsedLines.
	 * @param methodSignatures List of method signature ParsedLines.
	 * @param methodLines List of lists of ParsedLines for each method.
	 */
    public SecondPassParser(List<ParsedLine> globalLines, List<ParsedLine> methodSignatures ,
                            List<List<ParsedLine>> methodLines) {
        this.globalLines = globalLines;
        this.methodSignatures = methodSignatures;
        this.methodLines = methodLines;
    }

	/**
	 * Parses the stored ParsedLines.
	 */
    public void parse() throws SJavaParseException{
		updateMethodSignatures();
        checkGlobalLines();
        checkMethodLines();
    }

	/**
	 * Updates method signatures.
	 */
    private void updateMethodSignatures() throws SJavaParseException{
        for (ParsedLine method : methodSignatures) {
            addMethodSignature(method);
        }
    }
	private void addMethodSignature(ParsedLine methodSignatureLine) throws SJavaParseException{
		//check signature line
		if(methodSignatureLine == null || methodSignatureLine.getData()  == null) {
			throw new SJavaParseException(
					"Invalid method signature at line " +
							(methodSignatureLine == null ? "?" : methodSignatureLine.getLineNumber()));
		}
		MethodDeclarationData data = (MethodDeclarationData) methodSignatureLine.getData();
		String methodName = data.getMethodName();

		//Duplicate method name check
		if(methodTable.containsKey(methodName)) {
			throw new SJavaParseException(
					"Duplicate method name '" + methodName +
							"' at line " + methodSignatureLine.getLineNumber());
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
    private void checkGlobalLines() throws SJavaParseException{
        Map<String, VariableSymbol> globalScope = new HashMap<>();
		scopeStack.push(globalScope);
		for (ParsedLine line : globalLines) {
            addGlobalLine(line);
        }
    }

	/**
	 * Checks method lines.
	 */
	/**
	 * Checks method lines.
	 */
	private void checkMethodLines() throws SJavaParseException {
		for (List<ParsedLine> method : methodLines) {
			//Only the global scope should be on the stack
			while (scopeStack.size() > 1) {
				scopeStack.pop();
			}
			Map<String, VariableSymbol> methodScope = new HashMap<>();
			scopeStack.push(methodScope);

			ParsedLine signatureLine = method.get(0);
			MethodDeclarationData data = (MethodDeclarationData) signatureLine.getData();
			for (MethodDeclarationData.ParamInfo p : data.getParams()) {
				// Parameters are local variables initialized by the caller.
				if (methodScope.containsKey(p.getName())) {
					throw new SJavaParseException("Duplicate parameter name: " + p.getName());
				}
				methodScope.put(p.getName(), new VariableSymbol(p.getType(), p.isFinal(), true));
			}

			for (int i = 1; i < method.size(); i++) {
				checkMethodLine(method.get(i));
			}
			// the last line in the list is the CLOSING_BRACKET.
			// The line before it must be a RETURN.
			if (method.size() < 3 || method.get(method.size() - 2).getType() != LineType.RETURN) {
				throw new SJavaParseException("Method " + data.getMethodName() + " must end with 'return;'");
			}
		}
	}
	/** Represents a method symbol with its name and parameters. */
	private static final class MethodSymbol {
		private final String name;
		private final List<Param> params;

		private MethodSymbol(String name, List<Param> params) {
			this.name = name;
			this.params = params;
		}

		/**
		 * Gets the method name from the symbol.
		 * @return The method name.
		 */
		public String getName() { return name; }

		/**
		 * Gets the list of parameters for the method.
		 * @return The list of parameters.
		 */
		public List<Param> getParams() { return params; }

		private static final class Param {
			private final PrimitiveType type;
			private final boolean isFinal;
			private final String name; // optional but useful

			private Param(PrimitiveType type, boolean isFinal, String name) {
				this.type = type;
				this.isFinal = isFinal;
				this.name = name;
			}

			public PrimitiveType getType() { return type; }
			public boolean isFinal() { return isFinal; }
			public String getName() { return name; }
		}
	}

    private void addGlobalLine(ParsedLine globalLine) {
		if (globalLine == null) return;
		LineType type = globalLine.getType();
		switch(type){
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
	private void checkGlobalDeclaration(VarDeclarationData data) throws SJavaParseException {
		// The global scope is always the bottom-most map in our stack
		Map<String, VariableSymbol> globalScope = scopeStack.peek();

		for (VarDeclarationData.Item i : data.getItems()) {
			String name = i.getName();

			// 1. Check for duplicate global variable names [cite: 108]
			if (globalScope.containsKey(name)) {
				throw new SJavaParseException("Duplicate global variable name '" + name + "'");
			}

			boolean isInitialized = false;
			String valueToken = i.getValueToken();

			// 2. Handle Initialization [cite: 136, 142]
			if (valueToken != null) {
				validateValue(data.getType(), valueToken);
				isInitialized = true;
			} else if (data.isFinal()) {
				// Final variables MUST be initialized at declaration [cite: 136]
				throw new SJavaParseException("Final variable '" + name + "' not initialized");
			}

			// 3. Register the variable in the global scope map
			globalScope.put(name, new VariableSymbol(data.getType(), data.isFinal(), isInitialized));
		}
	}

	/**
	 * Checks global assignment lines.
	 */
	private void checkGlobalAssignment(VarAssignData data) throws SJavaParseException {
		for (VarAssignData.Item i : data.getItems()) {
			String name = i.getName();

			// Use resolve to find the variable in the current stack
			VariableSymbol symbol = resolve(name);

			if (symbol == null) {
				throw new SJavaParseException("Global variable '" + name + "' undeclared.");
			}
			if (symbol.isFinal()) {
				throw new SJavaParseException("Cannot assign a value to final global variable '" + name + "'");
			}
			validateValue(symbol.getType(), i.getValueToken());
			symbol.setInitialized(true);
		}
	}
	private void validateValue(PrimitiveType targetType, String token) throws SJavaParseException {
		// 1. Check if token is an existing variable
		VariableSymbol sourceVar = resolve(token);
		if (sourceVar != null) {
			if (!sourceVar.isInitialized()) {
				throw new SJavaParseException("Variable '" + token + "' used before initialization");
			}
			if (!isTypeCompatible(targetType, sourceVar.getType())) {
				throw new SJavaParseException("Type mismatch: cannot assign " + sourceVar.getType() + " to " + targetType);
			}
			return;
		}

		// 2. If not a variable, check if it's a compatible literal
		if (!isLiteralCompatible(targetType, token)) {
			throw new SJavaParseException("Invalid literal '" + token + "' for type " + targetType);
		}
	}

	private VariableSymbol resolve(String name) {
		for (Map<String, VariableSymbol> scope : scopeStack) {
			if (scope.containsKey(name)) {
				return scope.get(name);
			}
		}
		return null;
	}

	private boolean isTypeCompatible(PrimitiveType target, PrimitiveType source) {
		if (target == source) return true; // Direct match

		// s-Java widening rules:
		if (target == PrimitiveType.DOUBLE && source == PrimitiveType.INT) {
			return true;
		}
		if (target == PrimitiveType.BOOLEAN) {
			return source == PrimitiveType.INT || source == PrimitiveType.DOUBLE;
		}
		return false;
	}

	private boolean isLiteralCompatible(PrimitiveType target, String token) {
		switch(target) {
			case INT:
				return token.matches(INT_REGEX);
			case DOUBLE:
				// double accepts both double and int literals [cite: 99, 181]
				return token.matches(DOUBLE_REGEX) || token.matches(INT_REGEX);
			case STRING:
				return token.matches(STRING_REGEX);
			case CHAR:
				return token.matches(CHAR_REGEX);
			case BOOLEAN:
				// boolean accepts true, false, or any numeric literal [cite: 99, 181]
				return token.matches(BOOLEAN_REGEX) ||
						token.matches(DOUBLE_REGEX) ||
						token.matches(INT_REGEX);
			default:
				return false;
		}
	}

	private void checkMethodLine(ParsedLine methodLine) throws SJavaParseException {
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
				// Pop the most recent scope [cite: 63, 186]
				if (scopeStack.size() <= 1) {
					// This shouldn't happen if FirstPassParser is correct
					throw new SJavaParseException("Unexpected closing bracket.");
				}
				scopeStack.pop();
				break;

			case RETURN:
				// return; is already checked for syntax and location [cite: 209, 212]
				break;

			case METHOD_DECLARATION:
				// Already handled as the starting point in checkMethodLines
				break;

			default:
				break;
		}
	}
	private void handleLocalDeclaration(VarDeclarationData data) throws SJavaParseException {
		Map<String, VariableSymbol> currentScope = scopeStack.peek();
		for (VarDeclarationData.Item item : data.getItems()) {
			String name = item.getName();
			// Rule: Two local variables with same name cannot be in the same block [cite: 116]
			if (currentScope.containsKey(name)) {
				throw new SJavaParseException("Variable '" + name + "' already defined in this scope.");
			}

			boolean initialized = false;
			if (item.getValueToken() != null) {
				validateValue(data.getType(), item.getValueToken());
				initialized = true;
			} else if (data.isFinal()) {
				throw new SJavaParseException("Final local variable must be initialized[cite: 136].");
			}
			currentScope.put(name, new VariableSymbol(data.getType(), data.isFinal(), initialized));
		}
	}

	private void handleLocalAssignment(VarAssignData data) throws SJavaParseException {
		for (VarAssignData.Item item : data.getItems()) {
			VariableSymbol symbol = resolve(item.getName());
			if (symbol == null) {
				throw new SJavaParseException("Variable '" + item.getName() + "' undeclared.");
			}
			if (symbol.isFinal()) {
				throw new SJavaParseException("Cannot reassign final variable[cite: 138].");
			}
			validateValue(symbol.getType(), item.getValueToken());
			symbol.setInitialized(true);
		}
	}
	private void handleIfWhile(ConditionData data) throws SJavaParseException {
		// Rule: Conditions must be boolean compatible (boolean, int, or double) [cite: 236, 238, 239]
		for (String operand : data.getOperands()) {
			validateValue(PrimitiveType.BOOLEAN, operand);
		}
		// Push new scope for the block [cite: 118, 249]
		scopeStack.push(new HashMap<>());
	}
	private void handleMethodCall(MethodCallData data) throws SJavaParseException {
		MethodSymbol target = methodTable.get(data.getMethodName());
		if (target == null) {
			throw new SJavaParseException("Method '" + data.getMethodName() + "' not found[cite: 201].");
		}

		List<String> args = data.getArgs();
		List<MethodSymbol.Param> params = target.getParams();

		if (args.size() != params.size()) {
			throw new SJavaParseException("Argument count mismatch for " + target.getName() + "[cite: 207].");
		}

		for (int i = 0; i < args.size(); i++) {
			// Rule: Argument types must match parameter types (with widening) [cite: 206]
			validateValue(params.get(i).getType(), args.get(i));
		}
	}




}
