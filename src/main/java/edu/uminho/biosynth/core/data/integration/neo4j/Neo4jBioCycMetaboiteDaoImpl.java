package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class Neo4jBioCycMetaboiteDaoImpl extends AbstractNeo4jDao<BioCycMetaboliteEntity> implements MetaboliteDao<BioCycMetaboliteEntity> {

	private static Logger LOGGER = Logger.getLogger(Neo4jBioCycMetaboiteDaoImpl.class);
	
	private static Label compoundLabel = CompoundNodeLabel.BioCyc;
	private Label subDb = CompoundNodeLabel.MetaCyc;

	public Neo4jBioCycMetaboiteDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
	}
	
//	@Override
	public BioCycMetaboliteEntity find(Serializable id) {
		Node node = graphDatabaseService.findNodesByLabelAndProperty(subDb, "entry", id).iterator().next();
		BioCycMetaboliteEntity cpd = nodeToObject(node);
		return cpd;
	}

//	@Override
	public List<BioCycMetaboliteEntity> findAll() {
		ExecutionResult result = executionEngine.execute("MATCH (cpd:" + compoundLabel + ":" + subDb + ") RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<BioCycMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			BioCycMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}
	
	private String translateDb(String db) {
		String ret;
		switch (db) {
		case "META":
			ret = "MetaCyc";
			break;
		case "ECOLI":
			ret = "EcoCyc";
			break;
		default:
			throw new RuntimeException("DB NOT VALID " + db + " MUST BE <META, ECOLI>");
		}
		
		return ret;
	}

	@Override
	public Serializable save(BioCycMetaboliteEntity cpd) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("charge", cpd.getCharge());
		params.put("cmlMolWeight", cpd.getCmlMolWeight());
		params.put("gibbs", cpd.getGibbs());
		params.put("comment", cpd.getComment());
		params.put("smiles", cpd.getSmiles());
		params.put("inchi", cpd.getInchi());
		params.put("molWeight", cpd.getMolWeight());
		String biocycSubDb = translateDb(cpd.getSource());
		
		executionEngine.execute("MERGE (cpd:BioCyc:" + biocycSubDb +":Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, "
				+ "cpd.molWeight={molWeight}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, "
				+ "cpd.molWeight={molWeight}, cpd.proxy=false"
				, params);
		
		if (params.get("charge") != null) {
			executionEngine.execute("MERGE (c:Charge {charge:{charge}}) ", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (c:Charge {charge:{charge}}) MERGE (cpd)-[r:HasCharge]->(c)", params);
		}
		if (params.get("formula") != null) {
			executionEngine.execute("MERGE (f:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("inchi") != null) {
			executionEngine.execute("MERGE (i:InChI {inchi:{inchi}}) ", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (i:InChI {inchi:{inchi}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
		}
		if (params.get("smiles") != null) {
			executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		
		executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
		executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		
		for (String synonym : cpd.getSynonyms()) {
			params.put("name", synonym.toLowerCase());
			executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (BioCycMetaboliteCrossreferenceEntity xref : cpd.getCrossreferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			if (dbLabel.equals(CompoundNodeLabel.BiGG.toString())) {
				params.put("dbEntry", Integer.parseInt(dbEntry));
				LOGGER.debug(String.format("Generating Crossreference to %s - id:%s", dbLabel, dbEntry));
				//BiGG xrefs in BioCyc are match with id not the entry (which is the abbreviation)
				executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {id:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
				executionEngine.execute("MATCH (cpd1:" + biocycSubDb + " {entry:{entry}}), (cpd2:" + dbLabel + " {id:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);	
			} else {
				LOGGER.debug(String.format("Generating Crossreference to %s - entry:\"%s\"", dbLabel, dbEntry));
				executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
				executionEngine.execute("MATCH (cpd1:" + biocycSubDb + " {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
			}
		}
		
		for (String rxnId : cpd.getReactions()) {
			params.put("rxnId", rxnId);
			LOGGER.debug(String.format("Generating Reference to reaction; %s", rxnId));
			executionEngine.execute("MERGE (rxn:" + biocycSubDb + ":BioCyc:Reaction {entry:{rxnId}}) ON CREATE SET rxn.proxy=true", params);
			executionEngine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (rxn:" + biocycSubDb + " {entry:{rxnId}}) MERGE (cpd)-[r:ParticipatesIn]->(rxn)", params);
		}
		
		for (String cpdId : cpd.getParents()) {
			params.put("parentId", cpdId);
			LOGGER.debug(String.format("Generating Reference to Parent Compound; %s", cpdId));
			executionEngine.execute("MERGE (cpd:" + biocycSubDb + ":BioCyc:Compound {entry:{parentId}}) ON CREATE SET cpd.proxy=true", params);
			executionEngine.execute("MATCH (cpd1:" + biocycSubDb + " {entry:{entry}}), (cpd2:" + biocycSubDb + " {entry:{parentId}}) MERGE (cpd1)-[r:InstanseOf]->(cpd2)", params);
		}
		
		return null;
	}

	@Override
	protected BioCycMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		BioCycMetaboliteEntity cpd = new BioCycMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("formula")) cpd.setFormula( (String) node.getProperty("formula"));
		if (node.hasProperty("comment")) cpd.setComment( (String) node.getProperty("comment"));
		if (node.hasProperty("charge")) cpd.setCharge((Integer) node.getProperty("charge"));
		if (node.hasProperty("cmlMolWeight")) cpd.setCmlMolWeight((Double) node.getProperty("cmlMolWeight"));
		if (node.hasProperty("molWeight")) cpd.setMolWeight((Double) node.getProperty("molWeight"));
		if (node.hasProperty("inchi")) cpd.setInChI((String) node.getProperty("inchi"));
		if (node.hasProperty("smiles")) cpd.setSmiles((String) node.getProperty("smiles"));
		
		return cpd;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioCycMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioCycMetaboliteEntity saveMetabolite(
			BioCycMetaboliteEntity metabolite) {
		this.save(metabolite);
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(BioCycMetaboliteEntity.class.cast(entity));
	}

	@Override
	public BioCycMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:" + subDb + " {proxy:false}) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

}
