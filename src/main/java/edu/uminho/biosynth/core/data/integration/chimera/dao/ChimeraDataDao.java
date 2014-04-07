package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;

public interface ChimeraDataDao {
	public List<Long> getAllMetaboliteIds();
	public Map<String, Object> getEntryProperties(Long id);
	public List<Long> getClusterByQuery(String query);
	public Map<String, List<Object>> getCompositeNode(Long id);
	public CompositeMetaboliteEntity getCompositeMetabolite(Long id);
	public List<String> getAllProperties();
	public List<Long> listAllPropertyIds(String property);
	
	//TEMPORARY METHOD MISSING POJO FOR COMPOSE ENTRY
	public Node getCompositeNode(String entry, Label...labels);
}
