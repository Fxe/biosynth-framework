package pt.uminho.sysbio.biosynth.integration.model;


import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;

//import utilities.grammar.syntaxtree.AbstractSyntaxTree;
//import utilities.math.language.mathboolean.DataTypeEnum;
//import utilities.math.language.mathboolean.IValue;

public class GraphGPREntity extends AbstractGraphNodeEntity implements GPR{

	private static final long serialVersionUID = 1L;
	
	private NormalizedGPRTree normalizedTree;
	private String reaction;
	
	public GraphGPREntity(){
		
	}
	
	public GraphGPREntity(NormalizedGPRTree normalizedTree, String reaction){
		this.normalizedTree = normalizedTree;
		this.reaction = reaction;
	}

	public NormalizedGPRTree getNormalizedTree() {
		return normalizedTree;
	}

	public void setNormalizedTree(NormalizedGPRTree normalizedTree) {
		this.normalizedTree = normalizedTree;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	@Override
	public String getEntry() {
		// TODO Auto-generated method stub
		return (String)this.properties.get("entry");
	}

	public void setEntry(String entry) { properties.put("entry", entry);};

	
	@Override
	public String getRule() {
		// TODO Auto-generated method stub
		return (String) this.properties.get("rule");
	}
	
	public void setRule(String rule) { this.properties.put("rule", rule); }
	
	
}
