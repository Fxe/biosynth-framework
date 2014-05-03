package edu.uminho.biosynth.visualization.graphviz;

public interface VertexTransformer<V, N> {
	public N toDotNode(V vertex);
}
