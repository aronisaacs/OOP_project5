package ex5.secondpass;

import ex5.firstpass.ParsedLine;
import ex5.firstpass.PrimitiveType;
import ex5.firstpass.data.MethodDeclarationData;
import ex5.parser.SJavaParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
            updateMethodSignature(method);
        }
    }
	private void updateMethodSignature(ParsedLine methodSignatureLine) throws SJavaParseException{
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
        for (ParsedLine line : globalLines) {
            checkGlobalLine(line);
        }
    }

	/**
	 * Checks method lines.
	 */
    private void checkMethodLines() throws SJavaParseException{
        for (List<ParsedLine> method : methodLines) {
            for (ParsedLine parsedLine : method) {
                checkMethodLine(parsedLine);
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



    private void checkGlobalLine(ParsedLine globalLine) {}
    private void checkMethodLine(ParsedLine methodLine) {}





}
