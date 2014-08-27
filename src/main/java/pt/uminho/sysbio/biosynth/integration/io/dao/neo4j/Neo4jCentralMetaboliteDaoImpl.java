package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import edu.uminho.biosynth.core.data.integration.neo4j.AbstractNeo4jDao;

public class Neo4jCentralMetaboliteDaoImpl 
extends AbstractNeo4jDao<CentralMetaboliteEntity>
implements MetaboliteHeterogeneousDao<CentralMetaboliteEntity>{

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jCentralMetaboliteDaoImpl.class);
	private static Label METABOLITE_LABLE = DynamicLabel.label("Metabolite");
	
	@Override
	public CentralMetaboliteEntity getMetaboliteById(String tag, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralMetaboliteEntity getMetaboliteByEntry(String tag, String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralMetaboliteEntity saveMetabolite(String tag, CentralMetaboliteEntity entity) {
		boolean update = false;
		
//		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
//				DynamicLabel.label(entity.getMajorLabel()), 
//				"entry", 
//				entity.getEntry())) {
//			update = true;
//			LOGGER.debug("Update " + node);
//			
//			entity.setId(node.getId());
//		}
		
		if (!update) {
			Node node = graphDatabaseService.createNode();
			LOGGER.debug("Create " + node);
			
			node.addLabel(DynamicLabel.label(entity.getMajorLabel()));
			for (String label : entity.getLabels())
				node.addLabel(DynamicLabel.label(label));
			
			for (String key : entity.getProperties().keySet())
				node.setProperty(key, entity.getProperties().get(key));
			
			for (CentralMetaboliteProxyEntity proxy : entity.getCrossreferences()) {
				this.createOrLinkToProxy(node, proxy);
			}
			
			node.setProperty("proxy", false);
			
			entity.setId(node.getId());
		}
		
		return entity;
	}
	
	private void createOrLinkToProxy(Node parent, CentralMetaboliteProxyEntity proxy) {
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link To Node/Proxy " + proxyNode);
			create = false;
			
			parent.createRelationshipTo(proxyNode, DynamicRelationshipType.withName("HasCrossreferenceTo"));
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug("Create Proxy " + proxyNode);
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("proxy", true);
			parent.createRelationshipTo(proxyNode, DynamicRelationshipType.withName("HasCrossreferenceTo"));
		}
	}

	@Override
	protected CentralMetaboliteEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Long> getGlobalAllMetaboliteIds() {
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(METABOLITE_LABLE)) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<Long> getAllMetaboliteIds(String tag) {
		//TODO: verify label if valid
		
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<String> getAllMetaboliteEntries(String tag) {
		//TODO: verify label if valid
		
		List<String> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add((String)node.getProperty("entry"));
		}
		return result;
	}

}
