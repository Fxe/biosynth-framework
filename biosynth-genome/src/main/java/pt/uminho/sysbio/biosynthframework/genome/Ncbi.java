package pt.uminho.sysbio.biosynthframework.genome;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenbankReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderParser;

public class Ncbi {
  public static void main(String[] args) {
    try {
      DNASequence seq = new DNASequence("GTAC");
    } catch (CompoundNotFoundException e) {
      e.printStackTrace();
    }
    
    try {
      RNASequence rna = new RNASequence("AUGC");
    } catch (CompoundNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    try {
      ProteinSequence prot = new ProteinSequence("AW");
    } catch (CompoundNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    
    
    InputStream is = null;

    try {
      is = new FileInputStream("/var/genome/iMF721_1.txt");
      FastaReader<DNASequence, NucleotideCompound> fastaReader = 
          new FastaReader<>(is, 
                            new GenericFastaHeaderParser<>(), 
                            new DNASequenceCreator(
                                AmbiguityDNACompoundSet.getDNACompoundSet()));
      
      Map<String, DNASequence> seqs = null;
      while ((seqs = fastaReader.process(5)) != null) {
        for (String k : seqs.keySet()) {
          System.out.println(k);
        }
      };
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
    
//    try {
//      is = new FileInputStream("/var/genome/GCF_000005845.2_ASM584v2_genomic.gbff");
//      GenbankReader<DNASequence, NucleotideCompound> genbankReader = 
//          new GenbankReader<>(is, 
//                              new GenericGenbankHeaderParser<>(), 
//                              new DNASequenceCreator(
//                                  AmbiguityDNACompoundSet.getDNACompoundSet()));
//      
//      Map<String, DNASequence> seqs = null;
//      while ((seqs = genbankReader.process()) != null) {
//        for (String k : seqs.keySet()) {
//          System.out.println(k);
//          DNASequence seq = seqs.get(k);
//          System.out.println(seq.getDescription());
//          System.out.println(seq.getAccession());
//          System.out.println(seq.getAccession());
//        }
//      };
//    } catch (IOException | CompoundNotFoundException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } finally {
//      IOUtils.closeQuietly(is);
//    }

  }
}
