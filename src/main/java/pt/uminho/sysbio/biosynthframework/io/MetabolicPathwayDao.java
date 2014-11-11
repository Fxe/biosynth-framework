package pt.uminho.sysbio.biosynthframework.io;

public interface MetabolicPathwayDao {
	
	public void getMetabolicPathwayById(Long id);
	public void getMetabolicPathwayByEntry(String entry);
	
}
