package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jBioCycMetaboiteDaoImpl extends AbstractNeo4jDao implements IMetaboliteDao<BioCycMetaboliteEntity> {

	@Override
	public BioCycMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BioCycMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
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
		ExecutionEngine engine = new ExecutionEngine(graphdb);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("charge", cpd.getCharge());
		params.put("cmlMolWeight", cpd.getCmlMolWeight());
		params.put("gibbs", cpd.getGibbs());
		params.put("comment", cpd.getComment());
		params.put("smiles", cpd.getSmiles());
		params.put("inchi", cpd.getInChI());
		params.put("molWeight", cpd.getMolWeight());
		String biocycSubDb = translateDb(cpd.getSource());
		
		engine.execute("MERGE (cpd:BioCyc:" + biocycSubDb +":Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, cpd.molWeight={molWeight} "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.cmlMolWeight={cmlMolWeight}, cpd.gibbs={gibbs}, "
				+ "cpd.charge={charge}, cpd.comment={comment}, cpd.smiles={smiles}, cpd.inchi={inchi}, cpd.molWeight={molWeight}"
				, params);
		
		
		engine.execute("MERGE (c:Charge {charge:{charge}}) ", params);
		engine.execute("MERGE (n:Name {name:{name}}) ", params);
		
		if (params.get("formula") != null) {
			engine.execute("MERGE (f:Formula {formula:{formula}}) ", params);
			engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("inchi") != null) {
			engine.execute("MERGE (i:InChI {inchi:{inchi}}) ", params);
			engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (i:InChI {inchi:{inchi}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
		}
		if (params.get("smiles") != null) {
			engine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		
		engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (c:Charge {formula:{formula}}) MERGE (cpd)-[r:HasCharge]->(c)", params);
		engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		
		for (String synonym : cpd.getSynonyms()) {
			params.put("name", synonym.toLowerCase());
			engine.execute("MERGE (n:Name {name:{name}}) ", params);
			engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (BioCycMetaboliteCrossReferenceEntity xref : cpd.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			engine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ", params);
			engine.execute("MATCH (cpd1:" + biocycSubDb + " {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		for (String rxnId : cpd.getReactions()) {
			params.put("rxnId", rxnId);
			engine.execute("MERGE (rxn:" + biocycSubDb + ":Reaction {entry:{rxnId}}) ", params);
			engine.execute("MATCH (cpd:" + biocycSubDb + " {entry:{entry}}), (rxn:" + biocycSubDb + " {entry:{rxnId}}) MERGE (cpd)-[r:ParticipatesIn]->(rxn)", params);
		}
		
		for (String cpdId : cpd.getParents()) {
			params.put("parentId", cpdId);
			engine.execute("MERGE (cpd:" + biocycSubDb + ":Compound {entry:{parentId}}) ", params);
			engine.execute("MATCH (cpd1:" + biocycSubDb + " {entry:{entry}}), (cpd2:" + biocycSubDb + " {entry:{parentId}}) MERGE (cpd1)-[r:InstanseOf]->(cpd2)", params);
		}
		
		return null;
	}

}
