package edu.uminho.biosynth.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import edu.uminho.biosynth.core.data.io.dao.biodb.seed.HbmSeedMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.seed.JsonSeedMetaboliteDaoImpl;

public class SeedDaoFactory {
	
	private Resource resource;
	private SessionFactory sessionFactory;
	
	public SeedDaoFactory withFile(File file) {
		this.resource = new FileSystemResource(file);
		return this;
	}
	
	public SeedDaoFactory withSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}
	
	public JsonSeedMetaboliteDaoImpl buildJsonSeedMetaboliteDao() {
		JsonSeedMetaboliteDaoImpl daoImpl = new JsonSeedMetaboliteDaoImpl();
		
		daoImpl.setJsonFile(resource);
		
		return daoImpl;
	}
	
	public HbmSeedMetaboliteDaoImpl buildHbmSeedMetaboliteDao() {
		HbmSeedMetaboliteDaoImpl daoImpl = new HbmSeedMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			
		}
		
		daoImpl.setSessionFactory(sessionFactory);
		
		return daoImpl;
	}
}
