package pt.uminho.sysbio.biosynthframework.cheminformatics;

import java.util.HashSet;
import java.util.Set;

public class ChemicalStructure {
  public String inchi = null;
  public String formula = null;
  public InChIKey inchiKey = null;
  public Set<String> smiles = new HashSet<> ();
  public String name = null;
  public Set<String> names = new HashSet<> ();
  public String usmiles = null;
  public String mol = null;
  
  @Override
  public String toString() {
    return names.toString();
  }
  
  public Boolean isProtonatedVersion(ChemicalStructure structure) {
    Boolean result = null;
//    System.out.println("structure" + structure).;
    if (this.inchiKey != null && structure.inchiKey != null) {
      if (this.inchiKey.fikhb.equals(structure.inchiKey.fikhb) && 
          this.inchiKey.sikhb.equals(structure.inchiKey.sikhb)) {
        return true;
      }
    }
    
    return result;
  }
  
  public Boolean isStereoVersion(ChemicalStructure structure) {
    Boolean result = null;
    if (this.inchiKey != null && structure.inchiKey != null) {
      if (this.inchiKey.fikhb.equals(structure.inchiKey.fikhb)) {
        return true;
      }
    }
    
    return result;
  }
}
