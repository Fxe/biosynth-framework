package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SbmlObjectMetadata {
  
  private static final Logger logger = LoggerFactory.getLogger(SbmlObjectMetadata.class);
  
  public static enum SbmlObjectMetadataType {
    COMPOUND, PROTEIN,
  }
  
  public SbmlObjectMetadataType type;
  public String data;
  protected String formula;
  protected String inchi;
  protected String smiles;
  
  public Set<Pair<String, String>> references = new HashSet<> ();
  
  public String getFormula() { return formula;}
  public void setFormula(String formula) {
    if (formula == null || formula.trim().isEmpty()) {
      return;
    }
    if (this.formula != null && !this.formula.equals(formula)) {
      logger.warn("formula rewrite {} -> {}", this.formula, formula);
    }
    this.formula = formula;
  }
  
  public String getInchi() { return inchi;}
  public void setInchi(String inchi) {
    if (inchi == null || inchi.trim().isEmpty()) {
      return;
    }
    if (this.inchi != null && !this.inchi.equals(inchi)) {
      logger.warn("inchi rewrite {} -> {}", this.inchi, inchi);
    }
    this.inchi = inchi;
  }
  
  public String getSmiles() { return smiles;}
  public void setSmiles(String smiles) {
    if (smiles == null || smiles.trim().isEmpty()) {
      return;
    }
    if (this.smiles != null && !this.smiles.equals(smiles)) {
      logger.warn("smiles rewrite {} -> {}", this.smiles, smiles);
    }
    this.smiles = smiles;
  }

  @Override
  public String toString() {
    return String.format("%s - [%s]", type, references);
  }
}
