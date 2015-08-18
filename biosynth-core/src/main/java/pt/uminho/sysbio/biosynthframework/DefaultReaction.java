package pt.uminho.sysbio.biosynthframework;

import pt.uminho.sysbio.biosynthframework.util.BioSynthUtils;


public class DefaultReaction extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	
	public DefaultReaction() { }
	
	public DefaultReaction(DefaultReaction rxn) {
		super(rxn);
	}
	
  public DefaultReaction(long id, String entry, String name, 
                         String[] lhs, double[] lhsStoich, 
                         String[] rhs, double[] rhsStoich, Orientation orientation) {
    this.id = id;
    this.entry = entry;
    this.name = name;
    for (int i = 0; i < lhs.length; i++) {
      double value = i < lhsStoich.length ? lhsStoich[i] : 1.0;
      this.getLeftStoichiometry().put(lhs[i], value);
    }
    for (int i = 0; i < rhs.length; i++) {
      double value = i < rhsStoich.length ? rhsStoich[i] : 1.0;
      this.getRightStoichiometry().put(rhs[i], value);
    }
    this.orientation = orientation;
  }
	
	@Override
	public DefaultReaction clone() {
		return new DefaultReaction(this);
	}
	
	@Override
	public String toString() {
		String op = BioSynthUtils.toSymbol(getOrientation());
		
		return String.format("[%d:%s]<%s %s %s>", 
				this.id, this.entry, 
				this.reactantStoichiometry, op, this.productStoichiometry);
	}
}
