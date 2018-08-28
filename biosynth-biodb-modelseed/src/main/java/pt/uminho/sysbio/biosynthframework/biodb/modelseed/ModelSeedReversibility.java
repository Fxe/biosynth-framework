package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

public enum ModelSeedReversibility {
  
  LEFT_TO_RIGHT("<="),
  RIGHT_TO_LEFT("=>"),
  REVERSIBLE("<=>");
  
  private final String value;
  
  public static ModelSeedReversibility getEnum(String val) {
    switch (val) {
      case "LEFT_TO_RIGHT":
      case "<=": return ModelSeedReversibility.LEFT_TO_RIGHT;
      case "RIGHT_TO_LEFT":
      case "=>": return ModelSeedReversibility.RIGHT_TO_LEFT;
      case "REVERSIBLE":
      case "<=>": return ModelSeedReversibility.REVERSIBLE;
      default: throw new IllegalArgumentException("invalid value: " + val);
    }
  }
  
  private ModelSeedReversibility(String s) {
    this.value = s;
  }

  @Override
  public String toString() {
     return this.value;
  }
}
