package pt.uminho.sysbio.biosynth.integration.model;

import java.util.Set;


public class NormalizedGPRTree {
	
	private NormalizedGPRTreeNode treeNode;
	private Set<Set<String>> normalizedRule;

	public NormalizedGPRTree(Set<Set<String>> gpr){
		this.treeNode = this.convertToNormalizedGPRTree(gpr);
		this.normalizedRule = gpr;
	}
	
	public NormalizedGPRTreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(NormalizedGPRTreeNode treeNode) {
		this.treeNode = treeNode;
	}
	
	public NormalizedGPRTreeNode convertToNormalizedGPRTree(Set<Set<String>> gpr){
		if (gpr.size()==1){
			Set<String> element = gpr.iterator().next();
			if (element.size() == 1){
				this.treeNode = new GPRLeaf(element.iterator().next());
			} else {
				GPRAnd andElement = new GPRAnd();
				for (String el : element){
					andElement.addElement(el);
				}
				this.treeNode = andElement;

			}
		} else {
			GPROr orElement = new GPROr();
			for (Set<String> elements : gpr){
				orElement.addElement(elements);
			}
			this.treeNode = orElement;
		}
		return this.treeNode;
	}

	@Override
	public String toString() {
		String result = "(";
		if (this.normalizedRule.size() == 1){
			Set<String> element = this.normalizedRule.iterator().next();
			if (element.size() == 1){
				return element.iterator().next();
			} else {
				boolean firstAnd = true;
				for (String el : element){
					if (firstAnd){
						result += el;
						firstAnd = false;
					} else {
						result += " AND " + el;
					}
				}
			}
		} else{
			boolean firstOr = true;
			
			for (Set<String> el : this.normalizedRule) {
				if (firstOr){
					firstOr = false;
				} else {
					result += " OR ";
				}
				if (el.size() == 1){
					result += el.iterator().next();
				} else {
					result += "(";
					boolean firstAnd = true;
					for (String e : el){
						if (firstAnd){
							result += e;
							firstAnd = false;
						} else {
							result += " AND " + e;
						}
					}
					result += ")";
				}
				
			}
		}
		result += ")";
		return result;
	}
	
	
	
}
