package ex5.firstpass.data;

import ex5.firstpass.PrimitiveType;
import java.util.List;

/**
 * Data class representing a variable declaration with its type, finality, and items.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */
public final class VarDeclarationData implements LineData {
	private final boolean isFinal;
	private final PrimitiveType type;
	private final List<Item> items;

	/**
	 * Constructor for VarDeclarationData.
	 * @param isFinal boolean indicating if the variable is final
	 * @param type the primitive type of the variable
	 * @param items list of variable items (name and optional initializer)
	 */
	public VarDeclarationData(boolean isFinal, PrimitiveType type, List<Item> items) {
		this.isFinal = isFinal;
		this.type = type;
		this.items = items;
	}

	/**
	 * Gets whether the variable is declared as final.
	 * @return true if final, false otherwise
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * Gets the primitive type of the variable.
	 * @return the variable's primitive type
	 */
	public PrimitiveType getType() {
		return type;
	}

	/**
	 * Gets the list of variable items.
	 * @return list of variable items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 *  Data class representing a variable item with its name and optional initializer.
	 *  @author ron.stein
	 */
	public static final class Item {
		private final String name;
		private final String valueToken; // null if no initializer

		/**
		 * Constructor for Item.
		 * @param name the name of the variable
		 * @param valueToken the initializer token, or null if none
		 */
		public Item(String name, String valueToken) {
			this.name = name;
			this.valueToken = valueToken;
		}

		/**
		 * Gets the name of the variable.
		 * @return variable name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the initializer token of the variable, or null if none.
		 * @return initializer token or null
		 */
		public String getValueToken() {
			return valueToken;
		}
	}
}
