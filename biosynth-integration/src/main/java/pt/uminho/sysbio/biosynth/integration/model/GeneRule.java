package pt.uminho.sysbio.biosynth.integration.model;


public class GeneRule {
	
	private String name;
	
	private String rule;
	
	private String normalizedRule;
	
	private NormalizedGPRTree normalizedTree;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getNormalized_rule() {
		return normalizedRule;
	}
	public void setNormalizedRule(String normalizedRule) {
		this.normalizedRule = normalizedRule;
	}
	public NormalizedGPRTree getNormalizedTree() {
		return normalizedTree;
	}
	public void setNormalized_tree(NormalizedGPRTree normalizedTree) {
		this.normalizedTree = normalizedTree;
	}
	
	
}
