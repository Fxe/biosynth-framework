package edu.uminho.biosynth.visualization.escher;

public class EscherCanvas {
  public double x;
  public double y;
  public double width;
  public double height;
  
  @Override
  public String toString() {
    return String.format("EscherCanvas[(%.2f, %.2f) - [w:%.2f, h:%.2f]]", 
                         x, y, width, height);
  }
}
