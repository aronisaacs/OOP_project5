package ex5.firstpass;

import ex5.firstpass.SyntaxException;

public enum PrimitiveType {
		INT("int"),
		DOUBLE("double"),
		STRING("String"),
		BOOLEAN("boolean"),
		CHAR("char");

		private final String typeName;

		PrimitiveType(String typeName) {
			this.typeName = typeName;
		}
		public String getTypeName() {
			return typeName;
		}
		public static PrimitiveType fromTypeName(String typeName) throws SyntaxException {
			for (PrimitiveType type : PrimitiveType.values()){
				if (type.getTypeName().equals(typeName)){
					return type;
				}
			}
			throw new SyntaxException("Invalid primitive type: " + typeName);
		}
}
