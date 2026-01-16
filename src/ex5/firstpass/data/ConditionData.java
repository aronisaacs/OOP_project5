package ex5.firstpass.data;

import java.util.List;

/**
 * Data class representing a conditional statement (if/while)
 * with its operands and operators.
 * @see ex5.firstpass.data.LineData
 * @author ron.stein
 */
public final class ConditionData implements LineData {
	private final boolean isWhile;          // false => if
	private final List<String> operands;    // tokens between operators
	private final List<String> operators;   // "&&" / "||" in order

	/**
	 * Constructor for ConditionData.
	 * @param isWhile true if while condition, false if if condition
	 * @param operands list of operand strings
	 * @param operators list of operator strings
	 */
	public ConditionData(boolean isWhile, List<String> operands, List<String> operators) {
		this.isWhile = isWhile;
		this.operands = operands;
		this.operators = operators;
	}


	/**
	 * Gets the list of operands in the condition.
	 * @return list of operand strings
	 */
	public List<String> getOperands() { return operands; }
//	public List<String> getOperators() { return operators; }
//	public boolean isWhile() { return isWhile; }
}
