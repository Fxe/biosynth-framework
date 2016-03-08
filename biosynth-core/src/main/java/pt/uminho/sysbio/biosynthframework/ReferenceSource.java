package pt.uminho.sysbio.biosynthframework;

public enum ReferenceSource {
  ORIGINAL, //from source data 
  MANUAL, //user added
  INHERITED, //from related data
  UNKNOWN, 
  INFERRED //computed from lesser direct data
}
