package pt.uminho.sysbio.biosynthframework;

public interface ModelAdapter {
  public String getGpr(String mrxnEntry);

  public boolean isTranslocation(String mrxnEntry);

  public int getReactionSize(String mrxnEntry);

  public String getSpecieCompartment(String spiEntry);

  public Integer getSpecieDegree(String spiEntry);

  public boolean isDrain(String mrxnEntry);

  public boolean isBoundarySpecie(String spiEntry);

  public double[] getBounds(String mrxnEntry);
}
