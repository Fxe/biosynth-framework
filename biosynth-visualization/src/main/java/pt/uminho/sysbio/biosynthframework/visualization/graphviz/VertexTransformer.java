package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

public interface VertexTransformer<V, N> {
	public N toDotNode(V vertex);
}
