package ex5.secondpass;

import ex5.firstpass.PrimitiveType;

/**
 * VariableSymbol represents a variable's type, finality, and initialization status.
 * @author ron.stein
 */
public class VariableSymbol {
	private final PrimitiveType type;
	private final boolean isFinal;
	private boolean isInitialized;

	/**
	 * Constructor for VariableSymbol.
	 * @param type The primitive type of the variable.
	 * @param isFinal Whether the variable is final.
	 * @param isInitialized Whether the variable is initialized.
	 */
	public VariableSymbol(PrimitiveType type, boolean isFinal, boolean isInitialized) {
		this.type = type;
		this.isFinal = isFinal;
		this.isInitialized = isInitialized;
	}
	/**
	 * Gets the primitive type of the variable.
	 * @return The primitive type.
	 */
	public PrimitiveType getType() {
		return type;
	}

	/**
	 * Checks if the variable is final.
	 * @return true if final, false otherwise.
	 */
	public boolean isFinal() {
		return isFinal;
	}
	/**
	 * Checks if the variable is initialized.
	 * @return true if initialized, false otherwise.
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Sets the initialization status of the variable.
	 * @param initialized The new initialization status (boolean).
	 */
	public void setInitialized(boolean initialized) {
		isInitialized = initialized;
	}
}
