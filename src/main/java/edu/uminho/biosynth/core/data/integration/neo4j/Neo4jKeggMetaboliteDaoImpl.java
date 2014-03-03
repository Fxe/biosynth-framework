package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jKeggMetaboliteDaoImpl implements IMetaboliteDao<KeggMetaboliteEntity>{
	
	private static Label compoundLabel = CompoundNodeLabel.KEGG;
	private GraphDatabaseService graph;
	
	@Override
	public KeggMetaboliteEntity find(Serializable id) {
		Node node = graph.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		KeggMetaboliteEntity cpd = new KeggMetaboliteEntity();
		cpd.setId((Integer) node.getProperty("id"));
		return cpd;
	}

	@Override
	public Serializable save(KeggMetaboliteEntity cpd) {
		if (graph.findNodesByLabelAndProperty(compoundLabel, "entry", cpd.getEntry()).iterator().next() == null) {
			Node node = graph.createNode();
			node.setProperty("id", cpd.getId());
			node.setProperty("entry", cpd.getEntry());
			node.setProperty("formula", cpd.getFormula());
			node.setProperty("comment", cpd.getComment());
			node.setProperty("molWeight", cpd.getMolWeight());
			node.setProperty("remark", cpd.getRemark());
		}
		return null;
	}


}
