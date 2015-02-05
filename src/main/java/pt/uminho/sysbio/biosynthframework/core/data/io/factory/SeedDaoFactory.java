package pt.uminho.sysbio.biosynthframework.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.HbmSeedMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.HbmSeedReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.JsonSeedMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.JsonSeedReactionDaoImpl;

public class SeedDaoFactory {
	
	private Resource resource;
	private SessionFactory sessionFactory;
	private File path;
	
	public SeedDaoFactory withDirectory(String path) {
		this.path = new File(path);
		return this;
	}
	
	@Deprecated
	public SeedDaoFactory withFile(File file) {
		this.resource = new FileSystemResource(file);
		System.out.println(resource);
		return this;
	}
	
	public SeedDaoFactory withSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}
	
	public JsonSeedMetaboliteDaoImpl buildJsonSeedMetaboliteDao() {
		JsonSeedMetaboliteDaoImpl daoImpl = new JsonSeedMetaboliteDaoImpl(path.getAbsolutePath());
		
		return daoImpl;
	}
	
	public JsonSeedReactionDaoImpl buildJsonSeedReactionDao() {
		JsonSeedReactionDaoImpl daoImpl = new JsonSeedReactionDaoImpl(path.getAbsolutePath());
		
		return daoImpl;
	}
	
	public HbmSeedMetaboliteDaoImpl buildHbmSeedMetaboliteDao() {
		HbmSeedMetaboliteDaoImpl daoImpl = new HbmSeedMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			
		}
		
		daoImpl.setSessionFactory(sessionFactory);
		
		return daoImpl;
	}
	
	public HbmSeedReactionDaoImpl buildHbmSeedReactionDao() {
		
		
		if (sessionFactory == null) {
			
		}
		HbmSeedReactionDaoImpl daoImpl = new HbmSeedReactionDaoImpl(sessionFactory);
		return daoImpl;
	}
}
