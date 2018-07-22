package pt.uminho.sysbio.biosynthframework.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.sbml.reader.FbcXmlSbmlModelBoundReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.FluxnsXmlSbmlModelBoundReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.ParametersXmlSbmlModelBoundReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.XmlSbmlModelBoundReader;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SbmlConstraintBasedModelFactory implements ConstraintBasedModelFactory {

  private static final Logger logger = LoggerFactory.getLogger(SbmlConstraintBasedModelFactory.class);
  
  private Map<String, Integer> spiIndexMap = new HashMap<>();
  private Map<String, Integer> rxnIndexMap = new HashMap<>();
  public Map<String, XmlSbmlModelBoundReader> boundReaders = new HashMap<>();
  private XmlSbmlModel model;
  
  public SbmlConstraintBasedModelFactory(XmlSbmlModel model) {
    this.model = model;
    int i = 0;
    for (XmlSbmlSpecie o : model.getSpecies()) {
      String id = o.getAttributes().get("id");
      if (!DataUtils.empty(id)) {
        spiIndexMap.put(id, i++);
      } else {
        logger.warn("empty species id");
      }
    }
    i = 0;
    for (XmlSbmlReaction o : model.getReactions()) {
      String id = o.getAttributes().get("id");
      if (!DataUtils.empty(id)) {
        rxnIndexMap.put(id, i++);
      } else {
        logger.warn("empty reaction id");
      }
    }
    
    boundReaders.put("params", new ParametersXmlSbmlModelBoundReader(this.model));
    boundReaders.put("fluxns", new FluxnsXmlSbmlModelBoundReader(this.model));
    boundReaders.put("fbc", new FbcXmlSbmlModelBoundReader(this.model));
  }

  @Override
  public double[][] getMatrix() {
    double[][] matrix = new double[spiIndexMap.size()][rxnIndexMap.size()];
    
    for (XmlSbmlReaction o : model.getReactions()) {
      String id = o.getAttributes().get("id");
      Integer rxnIndex = this.rxnIndexMap.get(id);
      
      logger.debug("{} -[index]-> {}", id, rxnIndex);
      
      
      for (XmlObject xo : o.getListOfReactants()) {
        String s = xo.getAttributes().get("species");
        String value = xo.getAttributes().get("stoichiometry");
        Integer spiIndex = this.spiIndexMap.get(s);
        
        logger.debug("{} -[reactant]-> {} {} -> {}", id, value, s, spiIndex);
        
        if (DataUtils.empty(value)) {
          value = "1";
        }
        
        matrix[spiIndex][rxnIndex] = -1 * Double.parseDouble(value);
      }
      for (XmlObject xo : o.getListOfProducts()) {
        String s = xo.getAttributes().get("species");
        String value = xo.getAttributes().get("stoichiometry");
        Integer spiIndex = this.spiIndexMap.get(s);
        
        logger.debug("{} -[product]-> {} {} -> {}", id, value, s, spiIndex);
        
        if (DataUtils.empty(value)) {
          value = "1";
        }
        
        matrix[spiIndex][rxnIndex] = Double.parseDouble(value);
      }
    }
    
    return matrix;
  }

  @Override
  public double[][] getBounds() {
    double[][] bounds = new double[this.rxnIndexMap.size()][2];
    for (XmlSbmlReaction o : model.getReactions()) {
      String id = o.getAttributes().get("id");
      
      Tuple2<String> bound = null;
      String lb = null;
      String ub = null;
      for (String method : boundReaders.keySet()) {
        bound = boundReaders.get(method).getReactionBounds(id);
        if (bound != null) {
          lb = bound.e1;
          ub = bound.e2;
          break;
        }
      }
      
      Integer rxnIndex = this.rxnIndexMap.get(id);
      if (!DataUtils.empty(lb)) {
        bounds[rxnIndex][0] = Double.parseDouble(lb);
      } else {
        bounds[rxnIndex][0] = -1000;
      }
      if (!DataUtils.empty(ub)) {
        bounds[rxnIndex][1] = Double.parseDouble(ub);
      } else {
        bounds[rxnIndex][1] =  1000;
      }
      
    }
    return bounds;
  }

  @Override
  public Map<String, Integer> getSpeciesIndexMap() {
    return new HashMap<>(spiIndexMap);
  }

  @Override
  public Map<String, Integer> getReactionsIndexMap() {
    return new HashMap<>(rxnIndexMap);
  }

  @Override
  public ConstraintBasedModel build() {
    List<Range> bounds = new ArrayList<> ();
    double[][] bs = getBounds();
    for (int i = 0; i < rxnIndexMap.size(); i++) {
      bounds.add(new Range(bs[i][0], bs[i][1]));
    }
    return new ConstraintBasedModel(getMatrix(), bounds, spiIndexMap, rxnIndexMap);
  }
  


}
