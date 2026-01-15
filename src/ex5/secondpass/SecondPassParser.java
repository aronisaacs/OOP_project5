package ex5.secondpass;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.PrimitiveType;
import ex5.firstpass.data.*;
import ex5.lines.LineType;
import ex5.secondpass.SemanticException;
import ex5.secondpass.MethodSymbol;

import java.util.*;

/**
 * SecondPassParser processes ParsedLines to validate method signatures and check global and method lines.
 * @see SemanticException
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
    public void parse() throws SemanticException{
		updateMethodSignatures();
        checkGlobalLines();
        checkMethodLines();
    }

	/**
	 * Updates method signatures.
	 */
    private void updateMethodSignatures() throws SemanticException{
        for (ParsedLine method : methodSignatures) {
            addMethodSignature(method);
        }
    }
	private void addMethodSignature(ParsedLine methodSignatureLine) throws SemanticException{
		//check signature line
		if(methodSignatureLine == null || methodSignatureLine.getData()  == null) {
			throw new SemanticException(
					"Invalid method signature at line " +
							(methodSignatureLine == null ? "?" : methodSignatureLine.getLineNumber()));
		}
		MethodDeclarationData data = (MethodDeclarationData) methodSignatureLine.getData();
		String methodName = data.getMethodName();

		//Duplicate method name check
		if(methodTable.containsKey(methodName)) {
			throw new SemanticException(
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
    private void checkGlobalLines() throws SemanticException{
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

			ParsedLine signatureLine = method.get(0);
			MethodDeclarationData data = (MethodDeclarationData) signatureLine.getData();
			for (MethodDeclarationData.ParamInfo p : data.getParams()) {
				// Parameters are local variables initialized by the caller.
				if (methodScope.containsKey(p.getName())) {
					throw new SemanticException("Duplicate parameter name: " + p.getName());
				}
				methodScope.put(p.getName(), new VariableSymbol(p.getType(), p.isFinal(), true));
			}

			for (int i = 1; i < method.size(); i++) {
				checkMethodLine(method.get(i));
			}
			// the last line in the list is the CLOSING_BRACKET.
			// The line before it must be a RETURN.
			if (method.size() < 3 || method.get(method.size() - 2).getType() != LineType.RETURN) {
				throw new SemanticException("Method " + data.getMethodName() + " must end with 'return;'");
			}
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
	private void checkGlobalDeclaration(VarDeclarationData data) throws SemanticException {
		// The global scope is always the bottom-most map in our stack
		Map<String, VariableSymbol> globalScope = scopeStack.peek();

		for (VarDeclarationData.Item i : data.getItems()) {
			String name = i.getName();

			// 1. Check for duplicate global variable names
			if (globalScope.containsKey(name)) {
				throw new SemanticException("Duplicate global variable name '" + name + "'");
			}

			boolean isInitialized = false;
			String valueToken = i.getValueToken();

			if (valueToken != null) {
				validateValue(data.getType(), valueToken);
				isInitialized = true;
			} else if (data.isFinal()) {
				throw new SemanticException("Final variable '" + name + "' not initialized");
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
				throw new SemanticException("Global variable '" + name + "' undeclared.");
			}
			if (symbol.isFinal()) {
				throw new SemanticException("Cannot assign a value to final global variable '" + name + "'");
			}
			validateValue(symbol.getType(), i.getValueToken());
			symbol.setInitialized(true);
		}
	}
	private void validateValue(PrimitiveType targetType, String token) throws SemanticException {
		// 1. Check if token is an existing variable
		VariableSymbol sourceVar = resolve(token);
		if (sourceVar != null) {
			if (!sourceVar.isInitialized()) {
				throw new SemanticException("Variable '" + token + "' used before initialization");
			}
			if (!isTypeCompatible(targetType, sourceVar.getType())) {
				throw new SemanticException("Type mismatch: cannot assign " + sourceVar.getType() + " to " + targetType);
			}
			return;
		}

		// 2. If not a variable, check if it's a compatible literal
		if (!isLiteralCompatible(targetType, token)) {
			throw new SemanticException("Invalid literal '" + token + "' for type " + targetType);
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
					throw new SemanticException("Unexpected closing bracket.");
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
	private void handleLocalDeclaration(VarDeclarationData data) throws SemanticException {
		Map<String, VariableSymbol> currentScope = scopeStack.peek();
		for (VarDeclarationData.Item item : data.getItems()) {
			String name = item.getName();
			//Two local variables with same name cannot be in the same block
			if (currentScope.containsKey(name)) {
				throw new SemanticException("Variable '" + name + "' already defined in this scope.");
			}

			boolean initialized = false;
			if (item.getValueToken() != null) {
				validateValue(data.getType(), item.getValueToken());
				initialized = true;
			} else if (data.isFinal()) {
				throw new SemanticException("Final local variable must be initialized.");
			}
			currentScope.put(name, new VariableSymbol(data.getType(), data.isFinal(), initialized));
		}
	}


	private void handleLocalAssignment(VarAssignData data) throws SemanticException {
		Map<String, VariableSymbol> currentScope = scopeStack.peek();
		for (VarAssignData.Item item : data.getItems()) {
			VariableSymbol symbol = resolve(item.getName());
			if (symbol == null) {
				throw new SemanticException("Variable '" + item.getName() + "' undeclared.");
			}
			if (symbol.isFinal()) {
				throw new SemanticException("Cannot reassign a final variable.");
			}
			validateValue(symbol.getType(), item.getValueToken());
			//we need to create a local version so we don't mutate the global one
			VariableSymbol localUpdate = new
					VariableSymbol(symbol.getType(), symbol.isFinal(), true);
			currentScope.put(item.getName(), localUpdate);
		}
	}
	private void handleIfWhile(ConditionData data) throws SemanticException {
		//Conditions must be boolean compatible
		for (String operand : data.getOperands()) {
			validateValue(PrimitiveType.BOOLEAN, operand);
		}
		scopeStack.push(new HashMap<>());
	}
	private void handleMethodCall(MethodCallData data) throws SemanticException {
		MethodSymbol target = methodTable.get(data.getMethodName());
		if (target == null) {
			throw new SemanticException("Method '" + data.getMethodName() + "' not found.");
		}

		List<String> args = data.getArgs();
		List<MethodSymbol.Param> params = target.getParams();

		if (args.size() != params.size()) {
			throw new SemanticException("Argument count mismatch for " + target.getName());
		}

		for (int i = 0; i < args.size(); i++) {
			//Argument types must match parameter types
			validateValue(params.get(i).getType(), args.get(i));
		}
	}




}
