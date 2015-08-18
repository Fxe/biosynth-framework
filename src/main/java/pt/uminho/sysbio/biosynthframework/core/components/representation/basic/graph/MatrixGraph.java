package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph;

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import la.matrix.DenseMatrixDouble;

public class MatrixGraph<V, E> {
//implements IBinaryGraph<V, E> {

//	private DenseMatrixDouble matrix;
//	private List<V> vertices;
//	private Map<V, Integer> vertexIndexMap;
//	
//	public MatrixGraph() {
//		matrix = new DenseMatrixDouble(0, 0);
//		this.vertices = new ArrayList<V> ();
//		this.vertexIndexMap = new HashMap<V, Integer> ();
//	}
//	
//	public MatrixGraph(MatrixGraph<V, E> graph) {
//		this.matrix = new DenseMatrixDouble( graph.matrix);
//		this.vertices = new ArrayList<V> ();
//		this.vertexIndexMap = new HashMap<V, Integer> ();
//	}
//	
//	public MatrixGraph( Graph<V, E> graph) {
//		matrix = new DenseMatrixDouble( graph.size(), graph.size());
//		this.vertices = new ArrayList<V> ( graph.size());
//		this.vertexIndexMap = new HashMap<V, Integer> ();
//		int ptr = 0;
//		for (V vertex : graph.getVertices()) {
//			this.vertices.add(ptr, vertex);
//			this.vertexIndexMap.put(vertex, ptr);
//			ptr++;
//		}
//		for (IBinaryEdge<E, V> edge : graph.getEdges()) {
//			int srcIndex = this.vertexIndexMap.get(edge.getLeft());
//			int dstIndex = this.vertexIndexMap.get(edge.getRight());
//			this.matrix.setValue(srcIndex, dstIndex, edge.getWeight());
//		}
//	}
//	
//	@Override
//	public int size() {
//		return matrix.getRows();
//	}
//
//	@Override
//	public int order() {
//		return matrix.nonZero();
//	}
//
//	@Override
//	public void clear() {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("clear");
//	}
//
//	@Override
//	public void reverseGraph() {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("reverseGraph");
//	}
//
//	@Override
//	public boolean addVertex(V vertex) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("addVertex");
//	}
//
//	@Override
//	public boolean removeVertex(V vertex) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("removeVertex");
//	}
//
//	@Override
//	public Set<V> getVertices() {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("getVertexes");
//	}
//
//	@Override
//	public Set<V> getAdjacentVertices(V vertex) {
//		for (int i = 0; i < this.size(); i++) {
//			//if ( matrix.getValue(i, j))
//		}
//		throw new UnsupportedOperationException("getAdjacentVertices");
//	}
//
//	@Override
//	public boolean addEdge(IBinaryEdge<E, V> edge) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("addEdge(IBinaryEdge<E, V> edge)");
//	}
//
//	@Override
//	public E getEdge(V src, V dst) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("getEdge(V src, V dst)");
//	}
//
//	@Override
//	public IBinaryEdge<E, V> getEdge(E edge) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("getEdge(E edge)");
//	}
//
//	@Override
//	public Collection<IBinaryEdge<E, V>> getEdges() {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("getEdge()");
//	}
//
//	@Override
//	public void setWeight(V src, V dst, double w) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("setWeight");
//	}
//
//	@Override
//	public double getWeight(V src, V dst) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("getWeight(V src, V dst)");
//	}

}
