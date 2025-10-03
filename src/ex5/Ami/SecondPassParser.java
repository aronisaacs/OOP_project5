package ex5.Ami;

import java.util.List;

public class SecondPassParser {

    private final List<ParsedLine> globalLines;
    private final List<ParsedLine> methodSignatures;
    private final List<List<ParsedLine>> methodLines;

    public SecondPassParser(List<ParsedLine> globalLines, List<ParsedLine> methodSignatures ,
                            List<List<ParsedLine>> methodLines) {
        this.globalLines = globalLines;
        this.methodSignatures = methodSignatures;
        this.methodLines = methodLines;
    }

    public void parse() {
        checkGlobalLines();
        updateMethodSignatures();
        checkMethodLines();
    }

    private void updateMethodSignatures() {
        for (ParsedLine method : methodSignatures) {
            updateMethodSignature(method);
        }

    }

    private void checkGlobalLines() {
        for (ParsedLine line : globalLines) {
            checkGlobalLine(line);
        }
    }

    private void checkMethodLines() {
        for (List<ParsedLine> method : methodLines) {
            for (ParsedLine parsedLine : method) {
                checkMethodLine(parsedLine);
            }
        }
    }




    ///////////////////////////////// FOR FUTURE IMPLEMENTATION /////////////////////////////////
    private void updateMethodSignature(ParsedLine method) {}
    private void checkGlobalLine(ParsedLine line) {}
    private void checkMethodLine(ParsedLine parsedLine) {}





}
