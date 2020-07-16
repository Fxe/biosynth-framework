package pt.uminho.sysbio.biosynthframework.biodb.eutils;

public class EutilsDate {
  public String Year;
  public String Month;
  public String Day;
  
  @Override
  public String toString() {
    return String.format("%s/%s/%s", Month, Day, Year);
  }
}
