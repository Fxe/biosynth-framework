package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class MetabolicNetwork<I> {
  
  public Map<I, SimpleCompartment<I>> compartments = new HashMap<>();
  public Map<I, SimpleMetabolite<I>> metabolites = new HashMap<>();
  public BMap<I, I> metaboliteToCompartment = new BHashMap<>();
  public Map<I, SimpleModelReaction<I>> reactions = new HashMap<> ();
}
