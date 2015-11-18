package edu.uminho.biosynth.visualization.escher;

import java.util.Map;

public class EscherMap {
  
  public String map_name;
  public String map_id;
  public String map_description;
  public String homepage;
  public String schema;
  public Map<Long, EscherReaction> reactions;
  public Map<Long, EscherNode> nodes;
  public EscherCanvas canvas;
}
