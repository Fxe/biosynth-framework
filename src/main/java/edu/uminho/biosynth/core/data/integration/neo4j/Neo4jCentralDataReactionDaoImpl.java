package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class Neo4jCentralDataReactionDaoImpl extends AbstractNeo4jDao<CentralDataReactionEntity> implements ReactionDao<CentralDataReactionEntity> {

	public static Logger LOGGER = Logger.getLogger(Neo4jCentralDataReactionDaoImpl.class);
	
	public Neo4jCentralDataReactionDaoImpl() { }
	
	public Neo4jCentralDataReactionDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
	}

	@Override
	public CentralDataReactionEntity getReactionById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataReactionEntity getReactionByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataReactionEntity saveReaction(
			CentralDataReactionEntity rxn) {
		
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", rxn.getEntry());
		String major = rxn.getMajorLabel();
		
//		"MERGE (cpd:BioCyc:" + biocycSubDb +":Compound {entry:{entry}}) ON CREATE SET "
//				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
//				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
//				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, "
//				+ "cpd.molWeight={molWeight}, cpd.proxy=false "
//				+ "ON MATCH SET "
//				+ "cpd.updated_at=timestamp(), "
//				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
//				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, "
//				+ "cpd.molWeight={molWeight}, cpd.proxy=false"
		
		//{0} Labels -> Label1:Label2:Label3:etc ...
		String query_ = "MERGE (rxn:%s {entry:{entry}}) ON CREATE SET "
				+ "rxn.created_at=timestamp(), rxn.updated_at=timestamp(), rxn.proxy=false "
				+ "ON MATCH SET "
				+ "rxn.updated_at=timestamp(), rxn.proxy=false";
		
		String query = String.format(query_, rxn.getConcatenatedLabel(':'));
		this.executionEngine.execute(query, params);
		
		Node rxnNode = null;
		for (Node node : this.graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(rxn.getMajorLabel()), 
						"entry", rxn.getEntry())) {
			
			if (rxnNode != null) {
				LOGGER.error("Unique Constraint violation - check graph schema");
			}
			rxnNode = node;
		}
		
		for (String key : rxn.getProperties().keySet()) {
			rxnNode.setProperty(key, rxn.getProperties().get(key));
		}
		
		for (CentralDataReactionProperty property : rxn.getReactionStoichiometryProperties()) {
			System.out.println(property);
//			String cpdEntry = (String) property.getProperties().get("entry");
			String cpdEntry = (String) property.getUniqueKeyValue();
			String cpdMajor = property.getMajorLabel();
			
			Map<String, Object> propParams = new HashMap<> ();
			
			propParams.put("rxnEntry", rxn.getEntry());
			propParams.put("cpdEntry", cpdEntry);
			propParams.put("coefficient", property.getRelationshipProperties().get("coefficient"));
			propParams.put("value", property.getRelationshipProperties().get("value"));
			
//			propParams.put("stoichProps", property.getRelationshipProperties());

			this.mergeProxyCompoundNode(cpdEntry, cpdMajor);
			
			//{0} Reaction Major Label
			//{1} Compound Major Label
			//{2} RelationShip Labels Major Label
			String propQuery_ = "MATCH (rxn:%s {entry:{rxnEntry}}), (cpd:%s {entry:{cpdEntry}}) "
					+ "MERGE (rxn)-[r:%s {coefficient:{coefficient}, value:{value}}]->(cpd)";
			String propQuery = String.format(propQuery_, rxn.getMajorLabel(), cpdMajor, property.getRelationshipMajorLabel());
			System.out.println(propParams);
			System.out.println(propQuery);
			this.executionEngine.execute(propQuery, propParams);
		}
		
		return null;
	}
	
	private void mergeProxyCompoundNode(String entry, String labels) {
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", entry);
		
		String query_ = "MERGE (cpd:%s {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), cpd.proxy=true";
		String query = String.format(query_, labels);
		LOGGER.debug(query);
		System.out.println(params);
		this.executionEngine.execute(query, params);
	}

	@Override
	public Set<Serializable> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CentralDataReactionEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

}
