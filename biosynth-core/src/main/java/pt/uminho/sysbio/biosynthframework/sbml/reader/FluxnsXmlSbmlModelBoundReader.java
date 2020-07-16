package pt.uminho.sysbio.biosynthframework.sbml.reader;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;

public class FluxnsXmlSbmlModelBoundReader implements XmlSbmlModelBoundReader {

  protected XmlSbmlModel model;
  
  public FluxnsXmlSbmlModelBoundReader(XmlSbmlModel model) {
    this.model = model;
  }
  
  @Override
  public Tuple2<String> getReactionBounds(String id) {
    for (XmlSbmlReaction rxn : this.model.getReactions()) {
      if (id.equals(rxn.getId())) {
        return getReactionBounds(rxn);
      }
    }
    
    return null;
  }
  
  public Tuple2<String> getReactionBounds(XmlSbmlReaction rxn) {
    if (rxn.getListOfAnnotations() != null && 
        rxn.getListOfAnnotations().containsKey("fluxnsLimit")) {
      for (XmlObject o : rxn.getListOfAnnotations().get("fluxnsLimit")) {
        String lb = o.getAttributes().get("lower");
        String ub = o.getAttributes().get("upper");
        return new Tuple2<String>(lb, ub);
      }
    }
    
    return null;
  }

}
