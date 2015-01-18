package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationCluster;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationCurationDao;

public class Neo4jIntegrationCurationDaoImpl implements IntegrationCurationDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jIntegrationCurationDaoImpl.class);
	
	private GraphDatabaseService graphDatabaseService;
	
	public Neo4jIntegrationCurationDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	public CurationSet nodeToCurationSet(Node node) {
		if (node == null) {
			LOGGER.debug("Invalid curation set node: null");
			return null;
		}
		
		if (!node.hasLabel(CurationLabel.CurationSet)) {
			LOGGER.debug(String.format("Invalid curation set node: ", Neo4jUtils.getLabels(node)));
			return null;
		}
		
		CurationSet curationSet = new CurationSet();
		curationSet.setId(node.getId());
		curationSet.setEntry((String)node.getProperty("entry"));
		
		return curationSet;
	}
	
	public CurationCluster nodeToCurationMetabolite(Node node) {
		if (node == null) {
			LOGGER.debug("Invalid curation set node: null");
			return null;
		}
		if (!node.hasLabel(CurationLabel.CurationMetabolite)) {
			LOGGER.debug(String.format("Invalid curation set node: ", Neo4jUtils.getLabels(node)));
			return null;
		}
		
		CurationCluster curationCluster = new CurationCluster();
		curationCluster.setId(node.getId());
		curationCluster.setEntry((String)node.getProperty("entry"));
		
		return curationCluster;
	}
	
	@Override
	public Long saveCurationSet(CurationSet curationSet) {
		
		if (curationSet.getEntry() == null) {
			LOGGER.debug("Invalid set: null entry");
			return null;
		}
		
		Node node = graphDatabaseService.createNode();
		node.setProperty("entry", curationSet.getEntry());
		node.addLabel(CurationLabel.CurationSet);
		
		curationSet.setId(node.getId());
		return node.getId();
	}

	@Override
	public CurationSet getCurationSet(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		return nodeToCurationSet(node);
	}

	@Override
	public CurationSet getCurationSet(String entry) {
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(CurationLabel.CurationSet, "entry", entry));
		return nodeToCurationSet(node);
	}

	@Override
	public Long saveCurationCluster(CurationCluster curationCluster) {
		CurationSet curationSet = curationCluster.getCurationSet();
		
		if (curationSet == null) {
			LOGGER.warn("Invalid Curation Cluster: No curation set assigned.");
			return null;
		}
		Long curaSetId = curationSet.getId();
		if (curaSetId == null) {
			curaSetId = this.saveCurationSet(curationSet);
		}
		
		return null;
	}

	@Override
	public CurationCluster getCurationCluster(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		return nodeToCurationMetabolite(node);
	}

	@Override
	public CurationCluster getCurationCluster(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

}
