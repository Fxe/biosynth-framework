package edu.uminho.biosynth.core.components.representation.basic;

public class DefaultEdge<X, Y> {
	private X src;
	private Y dst;
	
	public DefaultEdge(X src, Y dst) {
		this.src = src;
		this.dst = dst;
	}
	
	public X getSource() {
		return this.src;
	}
	
	public Y getDestination() {
		return this.dst;
	}
	
	@Override
	public String toString() {
		String ret = this.src.toString() + " ->" + this.dst.toString();
		return ret;
	}
}
