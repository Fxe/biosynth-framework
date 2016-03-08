package pt.uminho.sysbio.biosynth.integration.model;

import java.util.HashSet;
import java.util.Set;

public class GPROr extends NormalizedGPRTreeNode{

	private Set<Set<String>> elements;
	
	public GPROr(){
		this.elements = new HashSet<Set<String>>();
	}
	
	public void addElement(Set<String> element){
		this.elements.add(element);
	}
	
	public Set<Set<String>> getElements(){
		return this.elements;
	}
	
}
