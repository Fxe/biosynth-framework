package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetabolitePropertyEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataReactionProperty;

public interface ChimeraDataDao {
	public List<Long> getAllMetaboliteIds();
	public Map<String, Object> getEntryProperties(Long id);
	public List<Long> getClusterByQuery(String query);
	public Map<String, List<Object>> getCompositeNode(Long id);
	public CompositeMetaboliteEntity getCompositeMetabolite(Long id);
	public List<String> getAllProperties();
	public List<Long> listAllPropertyIds(String property);
	
	public List<CentralDataMetabolitePropertyEntity> collectAllPropertyFromIds(String major, String uniqueKey, Long...ids);
	
	//TEMPORARY METHOD MISSING POJO FOR COMPOSE ENTRY
	public Node getCompositeNode(String entry, Label...labels);
	
	public int countByLabel(String label);
	public Set<Long> getEntitiesByLabel(String label);
	public Set<String> getAllMajorMetaboliteLabels();
	
	public Set<Long> collectEntityProperties(List<Long> entities, String...properties);
	
	
	public CentralDataReactionProperty getReactionProperty(Long id);
	public CentralDataMetabolitePropertyEntity getMetaboliteProperty(Long id);
}
