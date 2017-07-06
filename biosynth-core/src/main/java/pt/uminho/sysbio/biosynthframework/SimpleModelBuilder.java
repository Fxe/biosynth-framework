package pt.uminho.sysbio.biosynthframework;

import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;

public class SimpleModelBuilder {
  public static SimpleModelSpecie from(GenericMetabolite cpd) {
    return null;
  }
  
  public static SimpleModelSpecie from(XmlSbmlSpecie xspi) {
    SimpleModelSpecie result = new SimpleModelSpecie();
    result.id = xspi.getAttributes().get("id");
    result.name = xspi.getAttributes().get("name");
    return result;
  }
  
  public static SimpleModelReaction from(XmlSbmlReaction xrxn) {
    SimpleModelReaction result = new SimpleModelReaction();
    result.id = xrxn.getAttributes().get("id");
    result.name = xrxn.getAttributes().get("name");
    result.lb = 0.0;
    result.ub = 0.0;
    for (XmlObject o : xrxn.getListOfReactants()) {
      o.getAttributes().get("");
      o.getAttributes().get("");
    }
    return result;
  }
}
