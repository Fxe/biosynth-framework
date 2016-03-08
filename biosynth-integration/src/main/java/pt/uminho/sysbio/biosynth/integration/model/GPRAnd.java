package pt.uminho.sysbio.biosynth.integration.model;

import java.util.HashSet;
import java.util.Set;

public class GPRAnd extends NormalizedGPRTreeNode{

	private Set<String> elements;
	
	public GPRAnd(){
		this.elements = new HashSet<String>();
	}
	
	public void addElement(String element){
		this.elements.add(element);
	}
	
	public Set<String> getElements(){
		return this.elements;
	}
	
}
