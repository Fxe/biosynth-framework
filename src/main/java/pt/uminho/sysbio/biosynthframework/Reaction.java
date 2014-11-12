package pt.uminho.sysbio.biosynthframework;

import java.util.Map;



public interface Reaction {
	public Long getId();
	public String getEntry();
	public String getName();
	
	public Orientation getOrientation();
	public Map<String, Double> getLeftStoichiometry();
	public void setLeftStoichiometry(Map<String, Double> left);
	
	public Map<String, Double> getRightStoichiometry();
	public void setRightStoichiometry(Map<String, Double> right);
	
//	public List<StoichiometryPair> getLeft();
//	public Map<M, Double> getRight();
}
