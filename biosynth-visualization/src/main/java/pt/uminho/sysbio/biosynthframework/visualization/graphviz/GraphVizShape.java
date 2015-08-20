package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

public enum GraphVizShape {
	BOX, CIRCLE, POLYGON, EGG, TRIANGLE, OVAL,
	HOUSE, PENTAGON, DIAMOND, HEXAGON,
	INVHOUSE, INVTRIANGLE,
	PLAINTEXT, PLAIN,
	
	;
	
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
