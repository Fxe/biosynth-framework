package pt.uminho.sysbio.biosynthframework.cheminformatics;

import java.util.Map;
import java.util.Set;

public interface CheminformaticsModule {
  public String nameToInchi(String name);
  public String molToInchi(String mol);
  public String molToSmiles(String mol);
  public Map<String, Integer> molToAtomMap(String mol);
  
  public String smilesToCannonical(String smi);
  public String smilesToInchi(String smi);
  public Map<String, Integer> smilesToAtomMap(String smi);
  
  public String inchiToSmiles(String inchi);
  public String inchiToInchiKey(String inchi);
  public Map<String, Integer> inchiToAtomMap(String inchi);


  
  public String atomMapToFormula(Map<String, Integer> atomMap);
  public Map<String, Integer> resolveFormula(Set<String> formulas);
}
