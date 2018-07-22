package pt.uminho.sysbio.biosynthframework.sbml.reader;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;

public class ParametersXmlSbmlModelBoundReader implements XmlSbmlModelBoundReader {
  protected XmlSbmlModel model;
  
  public ParametersXmlSbmlModelBoundReader(XmlSbmlModel model) {
    this.model = model;
  }

  @Override
  public Tuple2<String> getReactionBounds(String id) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Tuple2<String> getReactionBounds(XmlSbmlReaction rxn) {
    Tuple2<String> bounds = null;
    String lb = null;
    String ub = null;
    for (XmlObject p : rxn.getListOfParameters()) {
      String parameterId = p.getAttributes().get("id");
      String name = p.getAttributes().get("name");
      if ((parameterId != null && 
           parameterId.toUpperCase().trim().equals("LOWER_BOUND")) ||
          (name != null && 
           name.toUpperCase().trim().equals("LOWER_BOUND"))) {
        lb = p.getAttributes().get("value");
      }
      if ((parameterId != null && 
           parameterId.toUpperCase().trim().equals("UPPER_BOUND")) || 
          (name != null && 
           name.toUpperCase().trim().equals("UPPER_BOUND"))) {
        ub = p.getAttributes().get("value");
      }
    }
    
    if (lb != null || ub != null) {
      bounds = new Tuple2<String>(lb, ub);
    }

    return bounds;
  }
}
