package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OperatingUnit<T> implements Serializable {
	
	private static final long serialVersionUID = 11L;
	private Set<T> alpha_;
	private Set<T> beta_;
	private String entry;
	private String id_ = null;
	private OperatingUnit<T> opposite_ = null;
	
	public OperatingUnit( Set<T> a, Set<T> b) {
		this.alpha_ = a;
		this.beta_ = b;
	}
	
	public OperatingUnit( T[] a, T[] b) {
		this.alpha_ = new HashSet<T> ( Arrays.asList(a));
		this.beta_ = new HashSet<T> ( Arrays.asList(b));
	}
	
	public OperatingUnit() {
		this.alpha_ = new HashSet<T> ();
		this.beta_ = new HashSet<T> ();
	}
	
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}

	public Set<T> getAlpha() {
		return this.alpha_;
	}
	
	public Set<T> getBeta() {
		return this.beta_;
	}
	
	public void setID(String id) {
		this.id_ = id;
	}
	
	public String getID() {
		return this.id_;
	}
	
	public void setOpposite( OperatingUnit<T> o) {
		this.opposite_ = o;
	}
	
	public OperatingUnit<T> getOpposite() {
		return this.opposite_;
	}
	
	/*
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (!(o instanceof OperatingUnit ))return false;
		OperatingUnit<T> test = (OperatingUnit<T>) o;
		return test.getBeta().containsAll(this.getBeta()) 
				&& test.getBeta().size() == this.getBeta().size()
				&& test.getAlpha().containsAll(this.getAlpha())
				&& test.getAlpha().size() == this.getAlpha().size();
	} */
	
	@Override
	public String toString() {
		if ( this.id_ != null) return id_;
		StringBuilder sb = new StringBuilder("(");
		sb.append( this.alpha_).append(",").append( this.beta_).append(')');
		return sb.toString();
	}
}
