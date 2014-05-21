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

import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class Neo4jKeggGlycanMetaboliteDaoImpl extends AbstractNeo4jDao<KeggGlycanMetaboliteEntity> implements MetaboliteDao<KeggGlycanMetaboliteEntity>{
	
	private static Label compoundLabel = CompoundNodeLabel.KEGG;
	
//	private String nullToString(Object obj) {
//		return obj==null?"null":obj.toString();
//	}
	public Neo4jKeggGlycanMetaboliteDaoImpl(GraphDatabaseService graphdb) { 
		super(graphdb);
	}
	
	

	@Override
	public KeggGlycanMetaboliteEntity find(Serializable id) {
		Node node = graphDatabaseService.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		KeggGlycanMetaboliteEntity cpd = nodeToObject(node);
		return cpd;
	}

	@Override
	public Serializable save(KeggGlycanMetaboliteEntity cpd) {

		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("cpdAttributes", attributes);
		params.put("entry", cpd.getEntry());
		if (cpd.getName() != null) {
			params.put("name", cpd.getName().toLowerCase());
		} else {
			params.put("name", null);
		}
		params.put("formula", cpd.getFormula());
		params.put("comment", cpd.getComment());
		params.put("compoundClass", cpd.getCompoundClass());
		params.put("remark", cpd.getRemark());
		params.put("mass", cpd.getMass());
		
		
		executionEngine.execute("MERGE (cpd:KEGG:Compound {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.comment={comment}, cpd.compoundClass={compoundClass}, cpd.remark={remark}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.comment={comment}, cpd.compoundClass={compoundClass}, cpd.remark={remark}, cpd.proxy=false"
				, params);
		
		//MERGE (t:Test {entry:"K"}) ON MATCH SET t.formula="foo"
		//
		
		if (cpd.getFormula() != null) {
			executionEngine.execute("MERGE (m:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:KEGG {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		
		for (String name : cpd.getNames()) {
			params.put("name", name.toLowerCase());
			executionEngine.execute("MERGE (m:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:KEGG {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}
		
		for (KeggGlycanMetaboliteCrossreferenceEntity xref : cpd.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
			executionEngine.execute("MATCH (cpd1:KEGG {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		return null;
	}

	@Override
	public List<KeggGlycanMetaboliteEntity> findAll() {
		ExecutionResult result = executionEngine.execute("MATCH (cpd:KEGG) RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<KeggGlycanMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			KeggGlycanMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}



	@Override
	protected KeggGlycanMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		KeggGlycanMetaboliteEntity cpd = new KeggGlycanMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("formula")) cpd.setFormula( (String) node.getProperty("formula"));
		if (node.hasProperty("comment")) cpd.setComment( (String) node.getProperty("comment"));
		if (node.hasProperty("remark")) cpd.setRemark( (String) node.getProperty("remark"));
		if (node.hasProperty("compoundClass")) cpd.setCompoundClass((String) node.getProperty("compoundClass"));
		return cpd;
	}



	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggGlycanMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggGlycanMetaboliteEntity saveMetabolite(
			KeggGlycanMetaboliteEntity metabolite) {
		this.save(metabolite);
		return null;
	}



	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(KeggGlycanMetaboliteEntity.class.cast(entity));
	}



	@Override
	public KeggGlycanMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:KEGG {proxy:false}) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}
}
