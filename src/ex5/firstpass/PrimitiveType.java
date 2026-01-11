package ex5.firstpass;

import ex5.parser.SJavaParseException;

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
		public static PrimitiveType fromTypeName(String typeName) throws SJavaParseException {
			for (PrimitiveType type : PrimitiveType.values()){
				if (type.getTypeName().equals(typeName)){
					return type;
				}
			}
			throw new SJavaParseException("Invalid primitive type: " + typeName);
		}
}
