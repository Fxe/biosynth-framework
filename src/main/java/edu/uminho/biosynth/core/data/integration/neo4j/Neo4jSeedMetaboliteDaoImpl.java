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

import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundStructureEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class Neo4jSeedMetaboliteDaoImpl extends AbstractNeo4jDao<SeedMetaboliteEntity> implements MetaboliteDao<SeedMetaboliteEntity> {

	private static Label compoundLabel = CompoundNodeLabel.Seed;
	
	public Neo4jSeedMetaboliteDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
	}

//	@Override
	public SeedMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public List<SeedMetaboliteEntity> findAll() {
		ExecutionResult result = executionEngine.execute("MATCH (cpd:" + compoundLabel + ") RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<SeedMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			SeedMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}

	@Override
	public Serializable save(SeedMetaboliteEntity cpd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("defaultCharge", cpd.getDefaultCharge());
		params.put("mass", cpd.getMass());
		params.put("deltaG", cpd.getDeltaG());
		params.put("deltaGErr", cpd.getDeltaGErr());
		params.put("abbreviation", cpd.getAbbreviation());
		if (!cpd.getStructures().isEmpty()) {
			params.put("smiles", cpd.getStructures().iterator().next().getStructure());
		} else {
			params.put("smiles", null);
		}
		
		executionEngine.execute("MERGE (cpd:Seed:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.defaultCharge={defaultCharge}, cpd.mass={mass}, "
				+ "cpd.deltaG={deltaG}, cpd.deltaGErr={deltaGErr}, cpd.abbreviation={abbreviation}, "
				+ "cpd.smiles={smiles}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.defaultCharge={defaultCharge}, cpd.mass={mass}, "
				+ "cpd.deltaG={deltaG}, cpd.deltaGErr={deltaGErr}, cpd.abbreviation={abbreviation}, "
				+ "cpd.smiles={smiles}, cpd.proxy=false"
				, params);
		
		if (params.get("defaultCharge") != null) {
			executionEngine.execute("MERGE (c:Charge {charge:{defaultCharge}}) ", params);
			executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (c:Charge {charge:{defaultCharge}}) MERGE (cpd)-[r:HasCharge]->(c)", params);
		}
		if (params.get("formula") != null) {
			executionEngine.execute("MERGE (f:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("name") != null) {
			executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (n:name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		if (params.get("smiles") != null) {
			executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		for (String synonym : cpd.getSynonyms()) {
			params.put("name", synonym.toLowerCase());
			executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (SeedCompoundCrossReferenceEntity xref : cpd.getCrossReferences()) {
//			System.out.println(xref);
			switch (xref.getType()) {
				case DATABASE:
					String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
					String dbEntry = xref.getValue(); //Also need to translate if necessary
					params.put("dbEntry", dbEntry);
					executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
					executionEngine.execute("MATCH (cpd1:Seed:Compound {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
					break;
				case MODEL:
					String modelId = xref.getRef();
					String value = xref.getValue();
					params.put("modelId", modelId);
					params.put("refId", value);
					executionEngine.execute("MERGE (m:Model {id:{modelId}}) ", params);
					executionEngine.execute("MATCH (cpd:Seed:Compound {entry:{entry}}), (m:Model {id:{modelId}}) MERGE (cpd)-[r:FoundIn {id:{refId}}]->(m)", params);
					break;
				default:
					throw new RuntimeException("unsupported type " + xref.getType());
			}
		}
		
		return null;
	}

	@Override
	protected SeedMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		SeedMetaboliteEntity cpd = new SeedMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("name")) cpd.setName((String) node.getProperty("name"));
		if (node.hasProperty("formula")) cpd.setFormula((String) node.getProperty("formula"));
		if (node.hasProperty("abbreviation")) cpd.setAbbreviation((String) node.getProperty("abbreviation"));
		if (node.hasProperty("defaultCharge")) cpd.setDefaultCharge((Integer) node.getProperty("defaultCharge"));
		if (node.hasProperty("mass")) cpd.setMass((Integer) node.getProperty("mass"));
		if (node.hasProperty("molWeight")) cpd.setDeltaG((Double) node.getProperty("deltaG"));
		if (node.hasProperty("inchi")) cpd.setDeltaGErr((Double) node.getProperty("deltaGErr"));
		if (node.hasProperty("smiles")) {
			SeedCompoundStructureEntity smiles = new SeedCompoundStructureEntity();
			cpd.getStructures().add(smiles);
			smiles.setStructure((String) node.getProperty("smiles"));
		}
		return cpd;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		List<Serializable> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:Seed) RETURN cpd.id AS ids").columnAs("ids");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

	@Override
	public SeedMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SeedMetaboliteEntity saveMetabolite(
			SeedMetaboliteEntity metabolite) {
		this.save(metabolite);
		return metabolite;
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(SeedMetaboliteEntity.class.cast(entity));
	}

	@Override
	public SeedMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:Seed) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

}
