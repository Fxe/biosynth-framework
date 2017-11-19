package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class SimpleMetabolicModel<I> {
  public I id;
  public Map<I, SimpleModelSpecie<I>> species = new HashMap<>();
  public Map<I, SimpleModelReaction<I>> reactions = new HashMap<>();
  public Map<I, SimpleCompartment<I>> compartments = new HashMap<>();
  public Map<I, ?> genes = new HashMap<>();
}
