package ex5.firstpass.data;

import ex5.firstpass.PrimitiveType;

import java.util.List;

public class MethodDeclarationData implements LineData {
	private final String methodName;
	private final List<ParamInfo> params;

	public MethodDeclarationData(String methodName, List<ParamInfo> params) {
		this.methodName = methodName;
		this.params = params;
	}
	public String getMethodName() {
		return methodName;
	}
	public List<ParamInfo> getParams() {
		return params;
	}
	public static final class ParamInfo {
		private final boolean isFinal;
		private final PrimitiveType type;
		private final String name;

		public ParamInfo(boolean isFinal, PrimitiveType type, String name) {
			this.isFinal = isFinal;
			this.type = type;
			this.name = name;
		}
		public boolean isFinal() { return isFinal; }
		public PrimitiveType getType() { return type; }
		public String getName() { return name; }
	}
}
