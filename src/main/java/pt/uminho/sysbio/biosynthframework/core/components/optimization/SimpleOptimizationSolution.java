package pt.uminho.sysbio.biosynthframework.core.components.optimization;

public class SimpleOptimizationSolution implements Solution {

	private String type;
	private Object value;
	
	@Override
	public void setProperty(String key, Object value) {
		this.type = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s::%s", type, value);
	}
}
