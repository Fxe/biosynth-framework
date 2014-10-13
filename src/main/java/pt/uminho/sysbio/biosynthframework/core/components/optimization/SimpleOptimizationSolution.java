package pt.uminho.sysbio.biosynthframework.core.components.optimization;

public class SimpleOptimizationSolution implements Solution<String>{

	private String type;
	private String value;
	
	@Override
	public void setProperty(String key, String value) {
		this.type = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s::%s", type, value);
	}
}
