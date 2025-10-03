package ex5.parser;

import ex5.lines.LineTypeFactory;
import ex5.lines.LineType;
import ex5.Ami.ParsedLine;


import java.util.ArrayList;
import java.util.List;

/**
 * First pass parser that processes the code lines to build symbol and method tables.
 * Handles method declarations, variable declarations, and scope management.
 * Throws SJavaParseException for syntax errors.
 * @see SJavaParseException
 * @see SymbolTable
 * @see MethodTable
 * @see ParsedLine
 * @see LineType
 * @author Aron Isaacs
 */
public class FirstPassParser {

    private  List<ParsedLine> globalLines;
    private List<ParsedLine> methodSignatures;
    private List<List<ParsedLine>> methodLines;
    private int currentScopeLevel = 0;


    /**
     * Constructor for FirstPassParser.
     * @param globalLines list to store global variable declarations
     * @param methodSignatures method table to populate
     * @param methodLines list to store lines of each method
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
     * @param codeLines list of code lines to parse
     * @throws SJavaParseException if a syntax error is encountered
     */
    public void parse(List<String> codeLines) throws SJavaParseException {
        int lineNumber = 0;
        for (String line : codeLines) {
            lineNumber ++;
            LineType type = LineTypeFactory.classify(line);
            ParsedLine parsedLine = type.parseStrict(line);
            ClassifyLines(type, lineNumber, parsedLine);
        }
        if (currentScopeLevel != 0) {
            throw new SJavaParseException("Unmatched brackets at end of file");
        }
    }

    /**
     * Handles the logic for different line types during parsing.
     * Updates scope level and method lines as needed.
     * @param type the type of the line
     * @param lineNumber the current line number
     * @param parsedLine the parsed representation of the line
     * @throws SJavaParseException if a syntax error is encountered
     */
    private void ClassifyLines(LineType type, int lineNumber, ParsedLine parsedLine) {
        //I think that the length of this function is acceptable given that it is a switch statement and each
        // case is relatively short.
        switch (type) {
            case EMPTY:
            case COMMENT:
                break;


            case METHOD_DECLARATION:
                if (currentScopeLevel != 0) {
                    throw new SJavaParseException("Nested method declaration at line " + lineNumber);
                }
                currentScopeLevel++;
                methodSignatures.add(parsedLine);
                methodLines.add(new ArrayList<>());
                methodLines.getLast().add(parsedLine);
                break;


            case IF_WHILE:
                if (currentScopeLevel == 0) {
                    throw new SJavaParseException("if/while outside of method at line " + lineNumber);
                }
                currentScopeLevel++;
                methodLines.getLast().add(parsedLine);
                break;


            case RETURN:
            case METHOD_CALL:
                if (currentScopeLevel == 0) {
                    throw new SJavaParseException(type + " not allowed in global scope at line " + lineNumber);
                }
                methodLines.getLast().add(parsedLine);
                break;


            case CLOSING_BRACKET:
                if (currentScopeLevel == 0) {
                    throw new SJavaParseException("Unmatched closing bracket at line " + lineNumber);
                }
                currentScopeLevel--;
                methodLines.getLast().add(parsedLine);
                break;


            case FINAL_VAR_DECLARATION:
            case NON_FINAL_VAR_DECLARATION:
            case VARIABLE_ASSIGNMENT:
                if (currentScopeLevel == 0) {
                    globalLines.add(parsedLine);
                } else {
                    methodLines.getLast().add(parsedLine);
                }
                break;
        }
    }
}
