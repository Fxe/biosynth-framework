package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class Neo4jMxnMetaboliteDaoImpl extends AbstractNeo4jDao<MnxMetaboliteEntity> implements MetaboliteDao<MnxMetaboliteEntity> {

	private static Label compoundLabel = CompoundNodeLabel.MetaNetX;
	
	public Neo4jMxnMetaboliteDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
	}

//	@Override
	public MnxMetaboliteEntity find(Serializable id) {
		Node node = graphDatabaseService.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		MnxMetaboliteEntity cpd = nodeToObject(node);
		return cpd;
	}

//	@Override
	public List<MnxMetaboliteEntity> findAll() {
		ExecutionResult result = executionEngine.execute("MATCH (cpd:MetaNetX) RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<MnxMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			MnxMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}

	@Override
	public Serializable save(MnxMetaboliteEntity cpd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("inchi", cpd.getInchi());
		params.put("smiles", cpd.getSmiles());
		params.put("originalSource", cpd.getOriginalSource());
		params.put("charge", cpd.getCharge());
		params.put("mass", cpd.getMass());
		
		executionEngine.execute("MERGE (cpd:MetaNetX:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, cpd.charge={charge},"
				+ "cpd.inchi={inchi}, cpd.smiles={smiles}, cpd.originalSource={originalSource}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, cpd.charge={charge},"
				+ "cpd.inchi={inchi}, cpd.smiles={smiles}, cpd.originalSource={originalSource}, cpd.proxy=false"
				, params);
		
		if (params.get("formula") != null) {
			executionEngine.execute("MERGE (m:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:MetaNetX:Compound {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("name") != null) {
			executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:MetaNetX:Compound {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		if (params.get("smiles") != null) {
			executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			executionEngine.execute("MATCH (cpd:MetaNetX:Compound {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		if (params.get("inchi") != null) {
			executionEngine.execute("MERGE (i:InChI {inchi:{inchi}}) ", params);
			executionEngine.execute("MATCH (cpd:MetaNetX:Compound {entry:{entry}}), (i:InChI {inchi:{inchi}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
		}
		if (params.get("charge") != null) {
			executionEngine.execute("MERGE (c:Charge {charge:{charge}}) ", params);
			executionEngine.execute("MATCH (cpd:MetaNetX:Compound {entry:{entry}}), (c:Charge {charge:{charge}}) MERGE (cpd)-[r:HasCharge]->(c)", params);	
		}
		for (MnxMetaboliteCrossReferenceEntity xref : cpd.getCrossreferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue();
			params.put("dbEntry", dbEntry);
			executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
			executionEngine.execute("MATCH (cpd1:MetaNetX:Compound {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		return null;
	}

	@Override
	protected MnxMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		MnxMetaboliteEntity cpd = new MnxMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("formula")) cpd.setFormula((String) node.getProperty("formula"));
		if (node.hasProperty("smiles")) cpd.setSmiles((String) node.getProperty("smiles"));
		if (node.hasProperty("inchi")) cpd.setInChI((String) node.getProperty("inchi"));
		if (node.hasProperty("mass")) cpd.setMass((Double) node.getProperty("mass"));
		if (node.hasProperty("originalSource")) cpd.setOriginalSource((String) node.getProperty("originalSource"));
		if (node.hasProperty("name")) cpd.setName((String) node.getProperty("name"));
		if (node.hasProperty("charge")) cpd.setCharge((Integer) node.getProperty("charge"));
		return cpd;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MnxMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MnxMetaboliteEntity saveMetabolite(
			MnxMetaboliteEntity metabolite) {
		this.save(metabolite);
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(MnxMetaboliteEntity.class.cast(entity));
	}

	@Override
	public MnxMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:MetaNetX) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

}
