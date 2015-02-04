package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractGraphEdgeEntity {
	
	public Long id;
	public Set<String> labels = new HashSet<> ();
	public Map<String, Object> properties = new HashMap<> ();
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("AbstractGraphEdgeEntity[%d]::%s", id, labels);
	}
}
