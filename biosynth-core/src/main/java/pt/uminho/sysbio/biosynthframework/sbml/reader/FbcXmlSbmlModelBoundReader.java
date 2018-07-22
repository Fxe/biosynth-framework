package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;

public class FbcXmlSbmlModelBoundReader implements XmlSbmlModelBoundReader {

  protected XmlSbmlModel model;
  protected Map<String, XmlObject> parameters = new HashMap<> ();
  
  public FbcXmlSbmlModelBoundReader(XmlSbmlModel model) {
    this.model = model;
    for (XmlObject xparams : model.getListOfParameters()) {
      String id = xparams.getAttributes().get("id");
      parameters.put(id, xparams);
    }
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
    String lfbcId = rxn.getAttributes().get("lowerFluxBound");
    String ufbcId = rxn.getAttributes().get("upperFluxBound");
    if (lfbcId != null) {
      XmlObject p = parameters.get(lfbcId);
      lfbcId = p.getAttributes().get("value");
    }
    if (ufbcId != null) {
      XmlObject p = parameters.get(ufbcId);
      ufbcId = p.getAttributes().get("value");
    }
    
    return new Tuple2<String>(lfbcId, ufbcId);
  }
}
