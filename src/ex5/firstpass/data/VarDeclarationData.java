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

	public VarDeclarationData(boolean isFinal, PrimitiveType type, List<Item> items) {
		this.isFinal = isFinal;
		this.type = type;
		this.items = items;
	}

	public boolean isFinal() { return isFinal; }
	public PrimitiveType getType() { return type; }
	public List<Item> getItems() { return items; }

	/**
	 * This was used for debugging.
	 * String representation of the variable declaration data.
	 * @return string describing the variable declaration
	 */
	@Override
	public String toString() {
		return "VarDeclarationData{final=" + isFinal +
				", type=" + type +
				", items=" + items + "}";
	}


	public static final class Item {
		private final String name;
		private final String valueToken; // null if no initializer

		public Item(String name, String valueToken) {
			this.name = name;
			this.valueToken = valueToken;
		}

		public String getName() { return name; }
		public String getValueToken() { return valueToken; }
	}
}
