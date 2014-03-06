package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jChebiMetaboliteDaoImpl extends AbstractNeo4jDao<ChebiMetaboliteEntity> implements IMetaboliteDao<ChebiMetaboliteEntity>{

	public Neo4jChebiMetaboliteDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ChebiMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChebiMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(ChebiMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChebiMetaboliteEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

}
