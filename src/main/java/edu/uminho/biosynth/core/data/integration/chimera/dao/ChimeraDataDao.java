package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.List;
import java.util.Map;

public interface ChimeraDataDao {
	public List<Long> getAllMetaboliteIds();
	public Map<String, Object> getEntry(Long id);
	public List<Long> getClusterByQuery(String query);
	public Map<String, List<Object>> getCompositeNode(Long id);
}
