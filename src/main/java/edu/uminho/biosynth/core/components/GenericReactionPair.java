package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GenericReactionPair extends AbstractBiosynthEntity implements Serializable{

	private static final long serialVersionUID = 454673L;
	
	private String type;
	private GenericMetabolite cpdEntry1;
	private GenericMetabolite cpdEntry2;
	private Set<String> relatedPairs;
	private Set<String> reactions;
	
	public GenericReactionPair(String id) {
		super(id);
		this.id = 0L;
		this.name = "unnamed";
		this.type = "undefined";
		this.relatedPairs = new HashSet<String> ();
		this.reactions = new HashSet<String> ();
	}
	
	public GenericReactionPair(String id, String name, String type) {
		super(id);
		this.id = 0L;
		this.name = name;
		this.type = type;
		this.relatedPairs = new HashSet<String> ();
		this.reactions = new HashSet<String> ();
	}
	
	public GenericReactionPair(GenericReactionPair rpr) {
		super(rpr.getEntry());
		this.id = rpr.getId();
		this.name = rpr.getName();
		this.type = rpr.getType();
		this.relatedPairs = new HashSet<String> ( rpr.getRelatedPairs());
		this.reactions = new HashSet<String> ( rpr.getReactions());
	}
	
	public void addRelatedPairs(Collection<String> pairs) {
		this.relatedPairs.addAll(pairs);
	}
	
	public void addReactions(Collection<String> reactions) {
		this.reactions.addAll(reactions);
	}
	
	public Set<String> getRelatedPairs() {
		return this.relatedPairs;
	}
	
	public Set<String> getReactions() {
		return this.reactions;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setEntry1(GenericMetabolite cpd) {
		this.cpdEntry1 = cpd;
	}
	
	public void setEntry2(GenericMetabolite cpd) {
		this.cpdEntry2 = cpd;
	}
	
	public GenericMetabolite getEntry1() {
		return this.cpdEntry1;
	}
	
	public GenericMetabolite getEntry2() {
		return this.cpdEntry2;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( this.id).append(':').append( this.getEntry()).append('[');
		sb.append( this.cpdEntry1).append("] <-> [").append( this.cpdEntry2).append(']');
		//sb.append("RP:").append( relatedPairs).append("R:").append( reactions);
		return sb.toString();
	}
}
