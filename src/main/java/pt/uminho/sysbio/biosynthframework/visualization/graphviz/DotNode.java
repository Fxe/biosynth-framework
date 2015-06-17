package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

public class DotNode {
	
	public String id;
	public String color = "black";
	public String fontcolor = "black";
	public String label = "";
	public GraphVizShape shape = GraphVizShape.CIRCLE;
	public int fontSize = 12;
	
	public String getFontcolor() { return fontcolor;}
  public void setFontcolor(String fontcolor) { this.fontcolor = fontcolor;}
  
  public String getColor() { return color;}
	public void setColor(String color) { this.color = color;}
	
	public String getLabel() { return label;}
	public void setLabel(String label) { this.label = label;}

	public GraphVizShape getShape() { return shape;}
	public void setShape(GraphVizShape shape) { this.shape = shape;}
	
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	@Override
	public String toString() {
		return String.format("%s [label=\"%s\", shape=%s, color=%s, fontcolor = %s]", id, label, shape, color, fontcolor);
	}
}
