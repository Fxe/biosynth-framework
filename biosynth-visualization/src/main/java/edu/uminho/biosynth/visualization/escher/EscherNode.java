package edu.uminho.biosynth.visualization.escher;

public class EscherNode {
  public String node_type;
  public double x;
  public double y;
  public String bigg_id;
  public String name;
  public double label_x;
  public double label_y;
  public boolean node_is_primary;
  
  @Override
  public String toString() {
    return String.format("EscherNode[%s - (%.2f, %.2f)]", node_type, x, y);
  }
}
