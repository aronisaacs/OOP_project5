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

	/**
	 * Constructor for MethodCallData.
	 * @param methodName name of the method being called
	 * @param args list of argument strings passed to the method
	 */
	public MethodCallData(String methodName, List<String> args) {
		this.methodName = methodName;
		this.args = args;
	}

	/**
	 * Gets the name of the method being called.
	 * @return method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the list of arguments passed to the method.
	 * @return list of argument strings
	 */
	public List<String> getArgs() {
		return args;
	}
}
