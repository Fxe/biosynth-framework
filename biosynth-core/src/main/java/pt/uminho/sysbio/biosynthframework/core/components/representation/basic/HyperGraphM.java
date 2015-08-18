package pt.uminho.sysbio.biosynthframework.core.components.representation.basic;


import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public class HyperGraphM {
	
	//private DoubleMatrix2D matrix;
	
	public HyperGraphM(DiHyperGraph<?, ?> graph) {
		//int reactions = graph.size();
		//int metabolites = graph.order();
		
		//matrix = DoubleFactory2D.rowCompressed.make(new double[metabolites][reactions]);
		/*
		for ( Set<?> edge : graph.getEdges()) {
			
		}*/
		//System.out.println( matrix.toString());
	}
	
	public double[] X(int i) {
		return null; //matrix.getNonZeros(arg0, arg1, arg2);
	}
	
	public double[] Y(int i) {
		return null;
	}
}
