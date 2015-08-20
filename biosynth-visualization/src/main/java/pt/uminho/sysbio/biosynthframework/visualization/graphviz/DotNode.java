package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.HashMap;
import java.util.Map;

public class DotNode {
	
	public String id;
	
	public Map<String, Object> propertyMap = new HashMap<> ();
	
	public String color = "black";
	public String fontcolor = "black";
	public String label = "";
	public GraphVizShape shape = GraphVizShape.CIRCLE;
	public int fontSize = 12;
	
	public void setProperty(String attribute, Object value) {
	  this.propertyMap.put(attribute, value);
	}
	
	public String getFontcolor() { return (String) propertyMap.get("fontcolor");}
  public void setFontcolor(String fontcolor) { this.propertyMap.put("fontcolor", fontcolor); }
  
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
	  String extraProps = "";
	  if (!propertyMap.isEmpty()) {
	    for (String attr : propertyMap.keySet()) {
	      Object v = propertyMap.get(attr);
	      String value = "";
	      if (v instanceof String) {
	        value = String.format("\"%s\"", v);
	      } else {
	        value = v.toString();
	      }
	      extraProps += String.format(", %s=%s", attr, value);
	    }
	  }
		return String.format("%s [label=%s, shape=%s, color=%s, fontcolor = %s %s]", id, label, shape, color, fontcolor, extraProps);
	}
}
