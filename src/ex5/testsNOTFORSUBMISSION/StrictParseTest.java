package ex5.testsNOTFORSUBMISSION;

import ex5.lines.LineType;
import ex5.lines.LineTypeFactory;
import ex5.firstpass.ParsedLine;
import ex5.main.SJavaParseException;

public class StrictParseTest {
	public static void main(String[] args) throws SJavaParseException {

		test("final int a = 521, b = 9;");
		test("int x;");
		test("a = 1, b = \"hi\";");
		test("void foo(int x, final double y) {");
		test("if (a || b && c) {");
		test("return;");
		test("}");
		test("foo(1, 2);");
		test("int x;     ");
		test("return;   ");
		test("}   ");

		fail("final int z;");
		fail("banana x = 3;");
		fail("a = ;");
	}

	private static void test(String line) throws SJavaParseException {
		LineType t = LineTypeFactory.classify(line);
		ParsedLine p = t.parseStrict(line, 1);
		System.out.println("OK: " + line + " → " + t + " → " + p.getData());
	}

	private static void fail(String line) {
		try {
			LineType t = LineTypeFactory.classify(line);
			t.parseStrict(line, 1);
			System.out.println("ERROR: should have failed: " + line);
		} catch (Exception e) {
			System.out.println("Expected failure: " + line);
		}
	}
}
