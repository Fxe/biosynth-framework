package edu.uminho.biosynth.visualization.escher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscherReaction {
  public String name;
  public String bigg_id;
  public boolean reversibility;
  public String label_x;
  public String label_y;
  public String gene_reaction_rule;
  public List<EscherGene> genes = new ArrayList<> ();
  public List<EscherReactionMetabolite> metabolites = new ArrayList<> ();
  public Map<Long, EscherReactionSegment> segments = new HashMap<> ();
}
