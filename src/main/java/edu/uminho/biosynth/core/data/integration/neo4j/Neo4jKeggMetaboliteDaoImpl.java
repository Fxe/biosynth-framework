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

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jKeggMetaboliteDaoImpl extends AbstractNeo4jDao<KeggCompoundMetaboliteEntity> implements IMetaboliteDao<KeggCompoundMetaboliteEntity>{
	
	private static Label compoundLabel = CompoundNodeLabel.KEGG;
	
//	private String nullToString(Object obj) {
//		return obj==null?"null":obj.toString();
//	}
	public Neo4jKeggMetaboliteDaoImpl(GraphDatabaseService graphdb) { 
		super(graphdb);
	}
	
	

	@Override
	public KeggCompoundMetaboliteEntity find(Serializable id) {
		Node node = graphdb.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		KeggCompoundMetaboliteEntity cpd = nodeToObject(node);
		return cpd;
	}

	@Override
	public Serializable save(KeggCompoundMetaboliteEntity cpd) {

		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("cpdAttributes", attributes);
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("comment", cpd.getComment());
		params.put("molWeight", cpd.getMolWeight());
		params.put("remark", cpd.getRemark());
		params.put("mass", cpd.getMass());
		
		
		engine.execute("MERGE (cpd:KEGG:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}"
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}"
				, params);
		
		//MERGE (t:Test {entry:"K"}) ON MATCH SET t.formula="foo"
		//
		
		if (cpd.getFormula() != null) {
			engine.execute("MERGE (m:Formula {formula:{formula}}) ", params);
			engine.execute("MATCH (cpd:KEGG {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		
		for (String name : cpd.getNames()) {
			params.put("name", name.toLowerCase());
			engine.execute("MERGE (m:Name {name:{name}}) ", params);
			engine.execute("MATCH (cpd:KEGG {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (KeggCompoundMetaboliteCrossreferenceEntity xref : cpd.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			engine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ", params);
			engine.execute("MATCH (cpd1:KEGG {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		return null;
	}

	@Override
	public List<KeggCompoundMetaboliteEntity> findAll() {
		ExecutionResult result = engine.execute("MATCH (cpd:KEGG) RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<KeggCompoundMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			KeggCompoundMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}



	@Override
	protected KeggCompoundMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("formula")) cpd.setFormula( (String) node.getProperty("formula"));
		if (node.hasProperty("comment")) cpd.setComment( (String) node.getProperty("comment"));
		if (node.hasProperty("remark")) cpd.setRemark( (String) node.getProperty("remark"));
		if (node.hasProperty("mass")) cpd.setMass((Double) node.getProperty("mass"));
		return cpd;
	}



	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteInformation(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggCompoundMetaboliteEntity saveMetaboliteInformation(
			KeggCompoundMetaboliteEntity metabolite) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Serializable save(Object entity) {
		return this.save(KeggCompoundMetaboliteEntity.class.cast(entity));
	}
}
