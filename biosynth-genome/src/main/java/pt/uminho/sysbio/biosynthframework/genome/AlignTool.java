package pt.uminho.sysbio.biosynthframework.genome;

import java.util.List;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.util.ConcurrencyTools;

public class AlignTool {
  
  private SubstitutionMatrix<AminoAcidCompound> matrix;
  
  public AlignTool() {
    matrix = SimpleSubstitutionMatrix.getBlosum62();
  }
  
  public List<SequencePair<ProteinSequence, AminoAcidCompound>> galign(List<ProteinSequence> seqs) {
    List<SequencePair<ProteinSequence, AminoAcidCompound>> alig = Alignments.getAllPairsAlignments(seqs,
        PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
    ConcurrencyTools.shutdown();
    return alig;
  }
  
  public List<SequencePair<ProteinSequence, AminoAcidCompound>> lalign(List<ProteinSequence> seqs) {
    List<SequencePair<ProteinSequence, AminoAcidCompound>> alig = Alignments.getAllPairsAlignments(seqs,
        PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
    ConcurrencyTools.shutdown();
    return alig;
  }
  
  public PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> galign(ProteinSequence seq1, ProteinSequence seq2) {
    PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> a = 
        Alignments.getPairwiseAligner(seq1, seq2, 
            PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
    ConcurrencyTools.shutdown();
    return a;
  }
  
  public PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> lalign(ProteinSequence seq1, ProteinSequence seq2) {
    PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> a = 
        Alignments.getPairwiseAligner(seq1, seq2, 
            PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
    ConcurrencyTools.shutdown();
    return a;
  }
  
}
