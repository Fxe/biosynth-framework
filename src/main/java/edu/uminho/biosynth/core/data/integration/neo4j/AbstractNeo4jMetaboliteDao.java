package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public abstract class AbstractNeo4jMetaboliteDao<M extends GenericMetabolite> 
extends AbstractNeo4jDao<M> implements MetaboliteDao<M> {

	private final String dbLabel;
	
	public AbstractNeo4jMetaboliteDao(String dbLabel) {
		this.dbLabel = dbLabel;
	}
	
	@Override
	public M getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public M getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public M saveMetabolite(M metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		List<Serializable> res = new ArrayList<> ();
		String query = String.format("MATCH (cpd:%s) RETURN cpd.id AS ids", dbLabel);
		Iterator<String> iterator = executionEngine.execute(query).columnAs("ids");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(M entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected abstract M nodeToObject(Node node);

}
