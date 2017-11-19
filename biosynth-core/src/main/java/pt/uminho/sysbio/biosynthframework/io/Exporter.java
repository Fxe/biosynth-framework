package pt.uminho.sysbio.biosynthframework.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import pt.uminho.sysbio.biosynthframework.SimpleCompartment;
import pt.uminho.sysbio.biosynthframework.SimpleMetabolicModel;
import pt.uminho.sysbio.biosynthframework.SimpleModelReaction;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;

public class Exporter {
  
  public static char sep = '\t';
  
  public static<I> void exportToFiles(SimpleMetabolicModel<I> model, String cpdFile, String rxnFile) {
    {
      String[] header = new String[]{"id", "name", "compartment", "compartment_name", "subcellular_compartment", "formula"};
      StringBuilder sb = new StringBuilder(StringUtils.join(header, sep));
      for (I id : model.species.keySet()) {
        SimpleModelSpecie<I> spi = model.species.get(id);
        I cmpId = spi.compartmentId;
        SimpleCompartment<I> cmp = model.compartments.get(cmpId);
        
        String[] spiRow = new String[]{
            id.toString(), 
            spi.name, 
            cmpId.toString(), 
            cmp.name, 
            cmp.scmp.toString(), 
            ""};
        sb.append('\n').append(StringUtils.join(spiRow, sep));
      }
      OutputStream os = null;
      try {
        os = new FileOutputStream(cpdFile);
        IOUtils.write(sb, os, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    {
      String[] header = new String[]{"id", "name", "left", "right", "left_stoich", "right_stoich", "lb", "ub"};
      StringBuilder sb = new StringBuilder(StringUtils.join(header, sep));
      
      for (I id : model.reactions.keySet()) {
        SimpleModelReaction<I> rxn = model.reactions.get(id);
        
        Object[] spiRow = new Object[]{
            id.toString(), 
            rxn.name, 
            "",
            "",
            "",
            "",
            rxn.bounds.lb,
            rxn.bounds.ub};
        sb.append('\n').append(StringUtils.join(spiRow, sep));
      }
      
      OutputStream os = null;
      try {
        os = new FileOutputStream(rxnFile);
        IOUtils.write(sb, os, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
