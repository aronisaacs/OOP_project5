package ex5.firstpass.data;

import java.util.List;

/**
 * Data class representing variable assignments.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */
public final class VarAssignData implements LineData {
	private final List<Item> items;

	/**
	 * Constructor for VarAssignData.
	 * @param items list of variable assignment items
	 */
	public VarAssignData(List<Item> items) {
		this.items = items;
	}

	/**
	 * Gets the list of variable assignment items.
	 * @return list of Item objects
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * Data class representing a single variable assignment item
	 * with its name and value token.
	 * @author ron.stein
	 */
	public static final class Item {
		private final String name;
		private final String valueToken;

		/**
		 * Constructor for Item.
		 * @param name the name of the variable
		 * @param valueToken the token representing the value assigned to the variable
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
		 * Gets the token representing the value assigned to the variable.
		 * @return value token
		 */
		public String getValueToken() {
			return valueToken;
		}
	}
}
