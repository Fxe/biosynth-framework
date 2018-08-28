package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

/**
 * 
 * @author Filipe Liu
 *
 */
public class GenomeReaction {
  //REACTIONS
  //LOAD    ;DIRECTIONALITY;COMPARTMENT;ASSOCIATED PEG   ;SUBSYSTEM                    ;CONFIDENCE;REFERENCE;NOTES
  //rxn05333;<=>           ;c          ;peg.1331|peg.2765;Fatty_Acid_Biosynthesis_FASII;3         ;Hope|MATT FILE|CHRIS FILE;NONE
  
  public String load;
  public ModelSeedReversibility directionality;
  public String compartment;
  public String associated_peg;
  public String subsystem = "NONE";
  public Integer confidence = 1;
  public String reference = "NONE";
  public String notes = "NONE";
  
  public Set<String> getGenes() {
    Set<String> genes = new HashSet<>();
    
    if (!DataUtils.empty(associated_peg)) {
      for (String s : this.associated_peg.split("\\|")) {
        genes.add(s.trim());
      }
    }
    
    return genes;
  }
  
  
  @Override
  public String toString() {
    List<Object> data = new ArrayList<>();
    data.add(load);
    data.add(directionality);
    data.add(compartment);
    data.add(associated_peg);
    data.add(subsystem);
    data.add(confidence);
    data.add(reference);
    data.add(notes);
    return StringUtils.join(data, ';');
  }
}
