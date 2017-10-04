package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class DefaultMetabolicNetworkFactory<I> implements MetabolicNetworkFactory {

  public Map<I, SimpleCompartment<I>> compartments = new HashMap<>();
  public Map<I, SimpleMetabolite<I>> metabolites = new HashMap<>();
  public BMap<String, String> metaboliteToCompartment = new BHashMap<>();
  public Map<I, SimpleModelReaction<I>> reactions = new HashMap<> ();
  public Map<I, Range> drains = new HashMap<> ();
  
  
  public DefaultMetabolicNetworkFactory<I> withSpecie(I id, String name, SubcellularCompartment scmp) {
    SimpleMetabolite<I> cpd = new SimpleMetabolite<>(id);
    cpd.name = name;
    this.metabolites.put(id, cpd);
    return this;
  }
  
  public void updateCompartment(String id, String name, SubcellularCompartment scmp) {
    SimpleCompartment<I> cmp = this.compartments.get(id);
    
    if (cmp != null) {
      if (name != null) {
        cmp.name = name;
      }
      
      if (scmp != null) {
        cmp.scmp = scmp;
      }
    } 
  }
  
  public DefaultMetabolicNetworkFactory<I> withReaction(
      I id, String name, Map<I, Double> stoichiometry, double lb, double ub) {
    SimpleModelReaction<I> srxn = new SimpleModelReaction<>(id, lb, ub);
    srxn.name = name;
    srxn.stoichiometry = new HashMap<>(stoichiometry);
    this.reactions.put(id, srxn);
    return this;
  }
  
  @Override
  public MetabolicNetwork<I> build() {
    MetabolicNetwork<I> mn = new MetabolicNetwork<>();
//    mn.compartments.addAll(compartments);
    return mn;
  }
}
