package ex5.firstpass.data;

import java.util.List;

/**
 * Data class representing variable assignments.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */
public final class VarAssignData implements LineData {
	private final List<Item> items;

	public VarAssignData(List<Item> items) {
		this.items = items;
	}

	public List<Item> getItems() { return items; }

	public static final class Item {
		private final String name;
		private final String valueToken;

		public Item(String name, String valueToken) {
			this.name = name;
			this.valueToken = valueToken;
		}

		public String getName() { return name; }
		public String getValueToken() { return valueToken; }
	}
}
