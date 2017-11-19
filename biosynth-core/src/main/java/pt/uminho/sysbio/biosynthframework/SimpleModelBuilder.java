package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModelAdapter;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;

public class SimpleModelBuilder<I> {
  
  public Map<I, SimpleCompartment<I>> cmpMap = new HashMap<>();
  public Map<I, SimpleModelSpecie<I>> spiMap = new HashMap<>();
  public Map<I, SimpleModelReaction<I>> rxnMap = new HashMap<>();
  
  public static SimpleModelBuilder<String> fromXmlSbmlModel(XmlSbmlModel xmodel) {
    XmlSbmlModelAdapter adapter = new XmlSbmlModelAdapter(xmodel);
    
    SimpleModelBuilder<String> builder = new SimpleModelBuilder<>();
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String id = xcmp.getAttributes().get("id");
      String name = xcmp.getAttributes().get("name");
      SimpleCompartment<String> cmp = new SimpleCompartment<>(id, name, SubcellularCompartment.UNKNOWN);
      builder.cmpMap.put(id, cmp);
    }
    
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String id = xspi.getAttributes().get("id");
      String name = xspi.getAttributes().get("name");
      String cmpId = xspi.getAttributes().get("compartment");
      SimpleModelSpecie<String> spi = new SimpleModelSpecie<>(id, name, cmpId);
      builder.spiMap.put(id, spi);
    }
    
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String id = xrxn.getAttributes().get("id");
      String name = xrxn.getAttributes().get("name");
      Range b = adapter.getBounds(id);
      SimpleModelReaction<String> rxn = new SimpleModelReaction<>(id, b.lb, b.ub);
      rxn.name = name;
      builder.rxnMap.put(id, rxn);
    }
    
    return builder;
  }
  
  public static SimpleModelSpecie from(GenericMetabolite cpd) {
    return null;
  }
  
//  public static SimpleModelSpecie from(XmlSbmlSpecie xspi) {
//    SimpleModelSpecie result = new SimpleModelSpecie();
//    result.id = xspi.getAttributes().get("id");
//    result.name = xspi.getAttributes().get("name");
//    return result;
//  }
  
  public static SimpleModelReaction from(XmlSbmlReaction xrxn) {
    SimpleModelReaction<String> result = new SimpleModelReaction<>(null, 0.0, 0.0);
    result.id = xrxn.getAttributes().get("id");
    result.name = xrxn.getAttributes().get("name");
//    result.lb = 0.0;
//    result.ub = 0.0;
    for (XmlObject o : xrxn.getListOfReactants()) {
      o.getAttributes().get("");
      o.getAttributes().get("");
    }
    return result;
  }
  
  public SimpleMetabolicModel<I> build() {
    SimpleMetabolicModel<I> model = new SimpleMetabolicModel<>();
    model.compartments.putAll(cmpMap);
    model.species.putAll(spiMap);
    model.reactions.putAll(rxnMap);
    return model;
  }
}
