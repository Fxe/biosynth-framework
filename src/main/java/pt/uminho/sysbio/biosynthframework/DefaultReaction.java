package pt.uminho.sysbio.biosynthframework;


public class DefaultReaction extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	
	public DefaultReaction() { }
	
	public DefaultReaction(DefaultReaction rxn) { 
		super(rxn);
	}
	
	@Override
	public DefaultReaction clone() {
		return new DefaultReaction(this);
	}
	
	@Override
	public String toString() {
		return String.format("[%d:%s]<%s, %s>", 
				this.id, this.entry, 
				this.reactantStoichiometry, this.productStoichiometry);
	}
}
