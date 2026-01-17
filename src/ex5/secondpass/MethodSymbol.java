package ex5.secondpass;

import ex5.firstpass.PrimitiveType;
import java.util.List;

/**
 * Represents a method symbol with its name and parameters.
 * Each parameter has a type, a final modifier, and a name.
 * @author ron.stein
 */
public class MethodSymbol {
	private final String name;
	private final List<Param> params;

	/**
	 * Constructor .
	 * @param name method name
	 * @param params list of parameters
	 */
	public MethodSymbol(String name, List<Param> params) {
		this.name = name;
		this.params = params;
	}

	/**
	 * Getters.
	 * @return method name and parameters
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getters.
	 * @return list of parameters
	 */
	public List<Param> getParams() {
		return params;
	}

	/**
	 * Nested class representing a single parameter.
	 * Each parameter has a type, a final modifier, and a name.
	 * @author ron.stein
	 */
	public static final class Param {
		private final PrimitiveType type;
		private final boolean isFinal;
		private final String name;

		/**
		 * Constructor.
		 * @param type parameter type
		 * @param isFinal whether the parameter is final
		 * @param name parameter name
		 */
		public Param(PrimitiveType type, boolean isFinal, String name) {
			this.type = type;
			this.isFinal = isFinal;
			this.name = name;
		}

		/**
		 * Getters.
		 * @return parameter type, final modifier, and name
		 */
		public PrimitiveType getType() {
			return type;
		}

		/**
		 * Getters.
		 * @return whether the parameter is final
		 */
		public boolean isFinal() {
			return isFinal;
		}

		/**
		 * Getters.
		 * @return parameter name
		 */
		public String getName() {
			return name;
		}
	}
}