package pt.uminho.sysbio.biosynthframework.genome;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.util.ConcurrencyTools;

public class NAlignTool implements Aligner {
  
  private SubstitutionMatrix<NucleotideCompound> matrix;
  
//  @Deprecated
//  public NAlignTool() {
//    matrix = new SimpleSubstitutionMatrix<NucleotideCompound>(DNACompoundSet.getDNACompoundSet(), new InputStreamReader(
//        SimpleSubstitutionMatrix.class.getResourceAsStream("ftp://ftp.ncbi.nih.gov/blast/matrices/NUC.4.4")), "nuc44");
//  }
  
  public NAlignTool(File mat) {
    matrix = new SimpleSubstitutionMatrix<NucleotideCompound>(DNACompoundSet.getDNACompoundSet(), new InputStreamReader(
        SimpleSubstitutionMatrix.class.getResourceAsStream(mat.getAbsolutePath())), "nuc44");
  }
  
  public NAlignTool(String mat) {
    InputStream is = new ByteArrayInputStream(mat.getBytes());
    matrix = new SimpleSubstitutionMatrix<NucleotideCompound>(DNACompoundSet.getDNACompoundSet(), 
                                                              new InputStreamReader(is), "nuc44");
  }
  
  public PairwiseSequenceAligner<DNASequence, NucleotideCompound> galign(DNASequence seq1, DNASequence seq2) {
    PairwiseSequenceAligner<DNASequence, NucleotideCompound> a = 
        Alignments.getPairwiseAligner(seq1, seq2, 
            PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
    ConcurrencyTools.shutdown();
    return a;
  }
  
  public PairwiseSequenceAligner<DNASequence, NucleotideCompound> lalign(DNASequence seq1, DNASequence seq2) {
    PairwiseSequenceAligner<DNASequence, NucleotideCompound> a = 
        Alignments.getPairwiseAligner(seq1, seq2, 
            PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
    
    ConcurrencyTools.shutdown();
    return a;
  }

  @Override
  public Object localAlignment(String seq1, String seq2) {
    try {
      DNASequence nseq1 = new DNASequence(seq1); 
      DNASequence nseq2 = new DNASequence(seq2);
      return this.lalign(nseq1, nseq2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Object globalAlignment(String seq1, String seq2) {
    try {
      DNASequence nseq1 = new DNASequence(seq1); 
      DNASequence nseq2 = new DNASequence(seq2);
      return this.galign(nseq1, nseq2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
