package ex5.firstpass.data;

import java.util.List;

/**
 * Data class representing a method call with its name and arguments.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */

public class MethodCallData implements LineData{
	private final String methodName;
	private final List<String> args;

	public MethodCallData(String methodName, List<String> args) {
		this.methodName = methodName;
		this.args = args;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<String> getArgs() {
		return args;
	}
}
