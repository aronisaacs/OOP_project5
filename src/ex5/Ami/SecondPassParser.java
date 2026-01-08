package ex5.Ami;

import java.util.List;

public class SecondPassParser {

    private final List<ParsedLine> globalLines;
    private final List<ParsedLine> methodSignatures;
    private final List<List<ParsedLine>> methodLines;

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
    public void parse() {
		updateMethodSignatures();
        checkGlobalLines();
        checkMethodLines();
    }

	/**
	 * Updates method signatures.
	 */
    private void updateMethodSignatures() {
        for (ParsedLine method : methodSignatures) {
            updateMethodSignature(method);
        }

    }

	/**
	 * Checks global lines.
	 */
    private void checkGlobalLines() {
        for (ParsedLine line : globalLines) {
            checkGlobalLine(line);
        }
    }

	/**
	 * Checks method lines.
	 */
    private void checkMethodLines() {
        for (List<ParsedLine> method : methodLines) {
            for (ParsedLine parsedLine : method) {
                checkMethodLine(parsedLine);
            }
        }
    }




    ///////////////////////////////// FOR FUTURE IMPLEMENTATION /////////////////////////////////
    private void updateMethodSignature(ParsedLine methodSignatureLine) {}
    private void checkGlobalLine(ParsedLine globalLine) {}
    private void checkMethodLine(ParsedLine methodLine) {}





}
