package ex5.firstpass;

import ex5.firstpass.SyntaxException;
/**
 * Enum representing primitive data types in s-Java.
 * @author ron.stein
 */
public enum PrimitiveType {
	/**
	 * Integer type
	 */
	INT(Constants.INT_NAME),

	/**
	 * Double type
	 */
	DOUBLE(Constants.DOUBLE_NAME),

	/**
	 * String type
	 */
	STRING(Constants.STRING_NAME),

	/**
	 * Boolean type
	 */
	BOOLEAN(Constants.BOOLEAN_NAME),

	/**
	 * Character type
	 */
	CHAR(Constants.CHAR_NAME);

	private static class Constants {
		// String Constants for types
		private static final String INT_NAME = "int";
		private static final String DOUBLE_NAME = "double";
		private static final String STRING_NAME = "String";
		private static final String BOOLEAN_NAME = "boolean";
		private static final String CHAR_NAME = "char";
		private static final String INVALID_TYPE_MESSAGE = "Invalid primitive type: ";
	}

	private final String typeName;

	PrimitiveType(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * Gets the string representation of the primitive type.
	 * @return the type name as a string
	 */
	public String getTypeName() {
			return typeName;
		}

	/**
	 * Converts a string type name to the corresponding PrimitiveType enum.
	 * @param typeName the string representation of the type
	 * @return the corresponding PrimitiveType
	 * @throws SyntaxException if the type name is invalid
	 */
	public static PrimitiveType fromTypeName(String typeName) throws SyntaxException {
		for (PrimitiveType type : PrimitiveType.values()){
			if (type.getTypeName().equals(typeName)){
				return type;
			}
		}
		throw new SyntaxException(Constants.INVALID_TYPE_MESSAGE + typeName);
	}
}
