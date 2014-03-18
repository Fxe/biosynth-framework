package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jIntegratedMetaboliteDao implements IMetaboliteDao<IntegratedMetaboliteEntity> {

	private GraphDatabaseService graphdb;
	private ExecutionEngine engine;
	
	public GraphDatabaseService getGraphdb() { return graphdb;}
	public void setGraphdb(GraphDatabaseService graphdb) {
		this.graphdb = graphdb;
		this.engine = new ExecutionEngine(graphdb);
	}

	@Override
	public IntegratedMetaboliteEntity getMetaboliteInformation(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMetaboliteEntity saveMetaboliteInformation(
			IntegratedMetaboliteEntity metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IntegratedMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(IntegratedMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
