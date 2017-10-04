package pt.uminho.sysbio.biosynthframework.genome;

public interface Aligner {
  public Object localAlignment(String seq1, String seq2);
  public Object globalAlignment(String seq1, String seq2);
}
