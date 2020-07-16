package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

public class ModelSeedGenomeFileBuilder {
  
  protected Set<GenomeReaction> genomeReactions = new HashSet<>();
  
  public static String COL_SEP = ";";
  public static String COL_ITEM_SEP = "!";
  public static String[] COLS = new String[]{
      "LOAD", "DIRECTIONALITY", "COMPARTMENT", "ASSOCIATED PEG", "SUBSYSTEM", "CONFIDENCE", "REFERENCE", "NOTES"};
  public static String HEADER = "REACTIONS\n" + Joiner.on(COL_SEP).join(COLS);
  
  public ModelSeedGenomeFileBuilder withRxnToGeneMapping(Map<String, Set<String>> mapping) {
    for (String rxn : mapping.keySet()) {
      Set<String> genes = mapping.get(rxn);
      GenomeReaction gr = new GenomeReaction();
      gr.load = rxn;
      gr.associated_peg = StringUtils.join(genes, COL_ITEM_SEP);
      gr.compartment = "c";
      gr.directionality = ModelSeedReversibility.REVERSIBLE;
      gr.subsystem = "NONE";
      gr.notes = "NONE";
      gr.reference = "KEGG";
      gr.confidence = 1;
      genomeReactions.add(gr);
    }
    return this;
  }
  
  public ModelSeedGenomeFileBuilder withGenomeReactions(Collection<GenomeReaction> reactions) {
    genomeReactions.addAll(reactions);
    return this;
  }
  
  /**
   * 
   * @param file
   */
  public void write(File file) throws IOException {
    try (OutputStream os = new FileOutputStream(file)) {
      os.write(HEADER.getBytes());
      for (GenomeReaction gr : genomeReactions) {
        os.write(("\n" + gr.toString()).getBytes());
      }
    }
  }
  
  public List<GenomeReaction> read(File file) throws IOException {
    List<GenomeReaction> grs = null;
    try (InputStream is = new FileInputStream(file)) {
      List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
      
      grs = new ArrayList<>();
      for (int i = 2; i < lines.size(); i++) {
        String[] p = lines.get(i).split(COL_SEP);
        GenomeReaction gr = new GenomeReaction();
        gr.load = p[0];
        gr.directionality = ModelSeedReversibility.getEnum(p[1]);
        gr.compartment = p[2];
        gr.associated_peg = p[3];
        gr.subsystem = p[4];
        gr.confidence = Integer.parseInt(p[5]);
        gr.reference = p[6];
        gr.notes = p[7];
        grs.add(gr);
      }
    }
    
    return grs;
  }
}
