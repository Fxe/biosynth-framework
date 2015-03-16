package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteNameEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class Neo4jChebiMetaboliteDaoImpl extends AbstractNeo4jDao<ChebiMetaboliteEntity> implements MetaboliteDao<ChebiMetaboliteEntity>{

	private static Logger LOGGER = LoggerFactory.getLogger(Neo4jChebiMetaboliteDaoImpl.class);
	
	public Neo4jChebiMetaboliteDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
		// TODO Auto-generated constructor stub
	}

//	@Override
	public ChebiMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public List<ChebiMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(ChebiMetaboliteEntity cpd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", cpd.getId());
		params.put("entry", cpd.getEntry());
		params.put("charge", cpd.getCharge());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("stars", cpd.getStars());
		params.put("mass", cpd.getMass());
		params.put("inchi", cpd.getInchi());
		params.put("inchikey", cpd.getInchiKey());
		params.put("smiles", cpd.getSmiles());
		params.put("source", cpd.getSource());
		params.put("parent", cpd.getParentId());
		
		executionEngine.execute("MERGE (cpd:ChEBI:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), cpd.parent={parent}, "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.stars={stars}, cpd.inchi={inchi}, cpd.inchikey={inchikey}, "
				+ "cpd.smiles={smiles}, cpd.source={source}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), cpd.parent={parent}, "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.stars={stars}, cpd.inchi={inchi}, cpd.inchikey={inchikey}, "
				+ "cpd.smiles={smiles}, cpd.source={source}, cpd.proxy=false"
				, params);
		
		if (params.get("charge") != null) {
			executionEngine.execute("MERGE (c:Charge {charge:{charge}}) ", params);
			executionEngine.execute("MATCH (cpd:ChEBI {entry:{entry}}), (c:Charge {charge:{charge}}) MERGE (cpd)-[r:HasCharge]->(c)", params);
		}
		if (params.get("formula") != null) {
			executionEngine.execute("MERGE (f:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:ChEBI {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("inchi") != null) {
			executionEngine.execute("MERGE (i:InChI {inchi:{inchi}}) ON CREATE SET i.inchikey={inchikey} ON MATCH SET i.inchikey={inchikey}", params);
			executionEngine.execute("MATCH (cpd:ChEBI {entry:{entry}}), (i:InChI {inchi:{inchi}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
		}
		if (params.get("smiles") != null) {
			executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			executionEngine.execute("MATCH (cpd:ChEBI {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		for (ChebiMetaboliteNameEntity name : cpd.getNames()) {
			params.put("name", name.getName().toLowerCase());
			executionEngine.execute("MERGE (n:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:ChEBI {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		for (ChebiMetaboliteCrossreferenceEntity xref: cpd.getCrossreferences()) {

			switch (xref.getType()) {
				case DATABASE:
					String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
					String dbEntry = xref.getValue();
					
					LOGGER.debug(String.format("Generating Crossreference to %s - entry:\"%s\"", dbLabel, dbEntry));
					
					params.put("dbEntry", dbEntry);
					executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
					executionEngine.execute("MATCH (cpd1:ChEBI:Compound {entry:{entry}}), "
							+ "(cpd2:" + dbLabel + " {entry:{dbEntry}}) "
							+ "MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
					break;
				case ECNUMBER:
					//TODO: implement ec numbers
					break;
				case CITATION:
					//TODO: implement citations
					break;
				case GENE:
					//TODO: implement genes
					break;
				case REACTION:
					//TODO: implement reactions
					break;
				default:
					LOGGER.warn(String.format("Unsupported crossreferecen type[%s] of %s", xref.getType(), xref));
					break;
			}
		}
		return null;
	}

	@Override
	protected ChebiMetaboliteEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		executionEngine.execute("MATCH (cpd:ChEBI) RETURN collect(cpd.entry);");
		return null;
	}

	@Override
	public ChebiMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChebiMetaboliteEntity saveMetabolite(
			ChebiMetaboliteEntity metabolite) {
		this.save(metabolite);
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(ChebiMetaboliteEntity.class.cast(entity));
	}

	@Override
	public ChebiMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:ChEBI {proxy:false}) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

}
