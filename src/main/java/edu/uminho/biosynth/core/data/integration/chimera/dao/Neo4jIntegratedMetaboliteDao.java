package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
	
@Deprecated
public class Neo4jIntegratedMetaboliteDao implements MetaboliteDao<IntegratedMetaboliteEntity> {

	private GraphDatabaseService graphdb;
	
	@SuppressWarnings("unused")
	private ExecutionEngine engine;
	
	public GraphDatabaseService getGraphdb() { return graphdb;}
	public void setGraphdb(GraphDatabaseService graphdb) {
		this.graphdb = graphdb;
		this.engine = new ExecutionEngine(graphdb);
	}

	@Override
	public IntegratedMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMetaboliteEntity saveMetabolite(
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
	public Serializable save(IntegratedMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Serializable saveMetabolite(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IntegratedMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getAllMetaboliteEntries() {
		// TODO Auto-generated method stub
		return null;
	}

}
