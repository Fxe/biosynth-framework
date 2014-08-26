package edu.uminho.biosynth.core.components.representation.basic.pgraph;

import java.util.HashSet;
import java.util.Set;

import edu.uminho.biosynth.core.components.representation.basic.graph.Graph;

public class ProcessGraph<T> implements Graph {
	private Set<T> P_;
	private Set<T> R_;
	private Set<T> M_;
	private Set< OperatingUnit<T>> O_;
	
	public ProcessGraph() {
		this.P_ = new HashSet<T>();
		this.R_ = new HashSet<T>();
		this.O_ = new HashSet<OperatingUnit<T>>();
		this.M_ = this.buildM();
	}
	
	public ProcessGraph( Set<T> P, Set<T> R, Set< OperatingUnit<T>> O) {
		this.P_ = P;
		this.R_ = R;
		this.O_ = O;
		this.M_ = this.buildM();
	}
	
	private Set<T> buildM() {
		HashSet<T> M = new HashSet<T> ();
		for ( OperatingUnit<T> op : O_) {
			M.addAll(op.getAlpha());
			M.addAll(op.getBeta());
		}
		
		return M;
	}
	
	public void rebuildM() {
		this.M_.clear();
		this.M_ = buildM();
	}
	
	public void addMaterials(Set<T> materials) {
		for (T t: materials) this.M_.add(t);
	}

	@Override
	public int size() {
		return M_.size();
	}

	@Override
	public int order() {
		return O_.size();
	}
	
	public boolean addOperatingUnit(OperatingUnit<T> op) {
		if (this.O_.add(op)) {
			this.M_.addAll(op.getAlpha());
			this.M_.addAll(op.getBeta());
		} else {
			return false;
		}
		return true;
	}

	/**
	 * @return the target species set
	 */
	public Set<T> getP() {
		return P_;
	}

	/**
	 * @return the initial species set
	 */
	public Set<T> getR() {
		return R_;
	}

	/**
	 * @return a copy of the species set
	 */
	public Set<T> getM() {
		Set<T> copy = new HashSet<T> ( this.M_);
		return copy;
	}

	/**
	 * @return the operating units set
	 */
	public Set<OperatingUnit<T>> getO() {
		return O_;
	}

	@Override
	public void clear() {
		this.R_.clear();
		this.P_.clear();
		this.O_.clear();
		this.M_.clear();
	}

	@Override
	public void reverseGraph() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( this.O_);
		return sb.toString();
	}

}
