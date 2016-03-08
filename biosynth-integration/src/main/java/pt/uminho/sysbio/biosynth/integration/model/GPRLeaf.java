package pt.uminho.sysbio.biosynth.integration.model;

public class GPRLeaf extends NormalizedGPRTreeNode{

	private String leaf;
	
	public GPRLeaf(String leaf){
		this.leaf = leaf;
	}

	public String getLeaf() {
		return leaf;
	}

	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}
	
	
	
}
