package ex5.firstpass.data;

import ex5.firstpass.PrimitiveType;
import java.util.List;

/**
 * Data class representing a method declaration
 * with its name and parameters.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */
public class MethodDeclarationData implements LineData {
	private final String methodName;
	private final List<ParamInfo> params;

	/**
	 * Constructor for MethodDeclarationData.
	 * @param methodName name of the method being declared
	 * @param params list of parameter info objects for the method
	 */
	public MethodDeclarationData(String methodName, List<ParamInfo> params) {
		this.methodName = methodName;
		this.params = params;
	}

	/**
	 * Gets the name of the method being declared.
	 * @return method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the list of parameters for the method.
	 * @return list of parameter info objects
	 */
	public List<ParamInfo> getParams() {
		return params;
	}

	/**
	 * Data class representing a method parameter
	 * with its finality, type, and name.
	 */
	public static final class ParamInfo {
		private final boolean isFinal;
		private final PrimitiveType type;
		private final String name;

		/**
		 * Constructor for ParamInfo.
		 * @param isFinal true if the parameter is final, false otherwise
		 * @param type the primitive type of the parameter
		 * @param name the name of the parameter
		 */
		public ParamInfo(boolean isFinal, PrimitiveType type, String name) {
			this.isFinal = isFinal;
			this.type = type;
			this.name = name;
		}

		/**
		 * Checks if the parameter is final.
		 * @return true if final, false otherwise
		 */
		public boolean isFinal() { return isFinal; }

		/**
		 * Gets the primitive type of the parameter.
		 * @return the primitive type
		 */
		public PrimitiveType getType() { return type; }

		/**
		 * Gets the name of the parameter.
		 * @return the parameter name
		 */
		public String getName() { return name; }
	}
}
