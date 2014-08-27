package edu.uminho.biosynth.integration.dao;

import java.io.Serializable;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.data.integration.neo4j.AbstractNeo4jDao;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetaboliteProxyEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;
import edu.uminho.biosynth.integration.CentralMetaboliteEntity;

public class Neo4jCentralMetaboliteDaoImpl 
extends AbstractNeo4jDao<CentralMetaboliteEntity>
implements MetaboliteDao<CentralMetaboliteEntity>{

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jCentralMetaboliteDaoImpl.class);
	
	@Override
	public CentralMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralMetaboliteEntity saveMetabolite(
			CentralMetaboliteEntity metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(CentralMetaboliteEntity entity) {
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
			
			for (CentralDataMetaboliteProxyEntity proxy : entity.getCrossreferences()) {
				this.createOrLinkToProxy(node, proxy);
			}
			
			node.setProperty("proxy", false);
			
			entity.setId(node.getId());
		}
		
		return entity;
	}
	
	private void createOrLinkToProxy(Node parent, CentralDataMetaboliteProxyEntity proxy) {
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

}
