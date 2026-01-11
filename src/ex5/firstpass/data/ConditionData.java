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

	public ConditionData(boolean isWhile, List<String> operands, List<String> operators) {
		this.isWhile = isWhile;
		this.operands = operands;
		this.operators = operators;
	}

	public boolean isWhile() { return isWhile; }
	public List<String> getOperands() { return operands; }
	public List<String> getOperators() { return operators; }
}
