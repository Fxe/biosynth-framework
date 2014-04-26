package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.awt.Color;

public class ColoredBinaryEdge<E, V> extends DefaultBinaryEdge<E, V> {

	private Color color;
	
	public ColoredBinaryEdge(E edge, V left, V right) {
		super(edge, left, right);
		this.color = new Color(0, 0, 0);
	}	
	
	public ColoredBinaryEdge(E edge, V left, V right, Color color) {
		super(edge, left, right);
		this.color = color;
	}	
	
	public ColoredBinaryEdge(E edge, V left, V right, double weight, Color color) {
		super(edge, left, right, weight);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
