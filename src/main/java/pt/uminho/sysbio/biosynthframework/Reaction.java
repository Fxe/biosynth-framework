package pt.uminho.sysbio.biosynthframework;



public interface Reaction {
	public Long getId();
	public String getEntry();
	
	public Orientation getOrientation();
	
//	public List<StoichiometryPair> getLeft();
//	public Map<M, Double> getRight();
}
