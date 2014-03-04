package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jKeggMetaboliteDaoImpl extends AbstractNeo4jDao implements IMetaboliteDao<KeggMetaboliteEntity>{
	
	private static Label compoundLabel = CompoundNodeLabel.KEGG;

	
//	private String nullToString(Object obj) {
//		return obj==null?"null":obj.toString();
//	}

	@Override
	public KeggMetaboliteEntity find(Serializable id) {
		Node node = graphdb.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		KeggMetaboliteEntity cpd = new KeggMetaboliteEntity();
		cpd.setId((Integer) node.getProperty("id"));
		return cpd;
	}

	@Override
	public Serializable save(KeggMetaboliteEntity cpd) {

		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("cpdAttributes", attributes);
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("comment", cpd.getComment());
		params.put("molWeight", cpd.getMolWeight());
		params.put("remark", cpd.getRemark());
		
		ExecutionEngine engine = new ExecutionEngine(graphdb);
		engine.execute("MERGE (cpd:KEGG:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}"
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}"
				, params);
		
		//MERGE (t:Test {entry:"K"}) ON MATCH SET t.formula="foo"
		//
		
		engine.execute("MERGE (m:Formula {formula:{formula}}) ", params);

		engine.execute("MATCH (cpd:KEGG {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		for (String name : cpd.getNames()) {
			params.put("name", name.toLowerCase());
			engine.execute("MERGE (m:Name {name:{name}}) ", params);
			engine.execute("MATCH (cpd:KEGG {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (KeggMetaboliteCrossReferenceEntity xref : cpd.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			engine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ", params);
			engine.execute("MATCH (cpd1:KEGG {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		return null;
	}

	@Override
	public List<KeggMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
