package pt.uminho.sysbio.biosynthframework.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.BiosynthHbmConnectionManager;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.HbmKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.HbmKeggDrugMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.HbmKeggGlycanMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.HbmKeggReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGlycanMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggReactionDaoImpl;

public class KeggDaoFactory {
	
	private SessionFactory sessionFactory;
	private Configuration configuration;
	
	private String localStorage = "./";
	private boolean useLocalStorage = false;
	private boolean saveLocalStorage = false;
	
	public KeggDaoFactory usingLocalStorage() {
		this.useLocalStorage = true;
		return this;
	}
	public KeggDaoFactory savingToLocalStorage() {
		this.saveLocalStorage = true;
		return this;
	}
	public KeggDaoFactory withLocalStorage(String localStorage) {
		this.localStorage = localStorage;
		return this;
	}
	
	public KeggDaoFactory withSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}
	
	public KeggDaoFactory withHibernateConfigurationFile(File file) {
		configuration = new Configuration().configure(file);
		return this;
	}
	
	public RestKeggCompoundMetaboliteDaoImpl buildRestKeggCompoundMetaboliteDao() {
		RestKeggCompoundMetaboliteDaoImpl daoImpl = new RestKeggCompoundMetaboliteDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		
		return daoImpl;
	}
	
	public RestKeggGlycanMetaboliteDaoImpl buildRestKeggGlycanMetaboliteDao() {
		RestKeggGlycanMetaboliteDaoImpl daoImpl = new RestKeggGlycanMetaboliteDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		
		return daoImpl;
	}
	
	public RestKeggDrugMetaboliteDaoImpl buildRestKeggDrugMetaboliteDao() {
		RestKeggDrugMetaboliteDaoImpl daoImpl = new RestKeggDrugMetaboliteDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		
		return daoImpl;
	}
	
	public RestKeggReactionDaoImpl buildRestKeggReactionDao() {
		RestKeggReactionDaoImpl daoImpl = new RestKeggReactionDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		
		return daoImpl;
	}
	
	
	public HbmKeggCompoundMetaboliteDaoImpl buildHbmKeggCompoundMetaboliteDao() {
		HbmKeggCompoundMetaboliteDaoImpl daoImpl = new HbmKeggCompoundMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					KeggCompoundMetaboliteEntity.class, 
					KeggCompoundMetaboliteCrossreferenceEntity.class);
		}
		
		BiosynthHbmConnectionManager.registerSessionFactory(daoImpl, sessionFactory);
		
		daoImpl.setSessionFactory(sessionFactory);
		return daoImpl;
	}
	
	public HbmKeggGlycanMetaboliteDaoImpl buildHbmKeggGlycanMetaboliteDao() {
		
		
		if (sessionFactory == null) {
			initialize(
					KeggGlycanMetaboliteEntity.class, 
					KeggGlycanMetaboliteCrossreferenceEntity.class);
		}
		
		HbmKeggGlycanMetaboliteDaoImpl daoImpl = new HbmKeggGlycanMetaboliteDaoImpl(sessionFactory);
		
		BiosynthHbmConnectionManager.registerSessionFactory(daoImpl, sessionFactory);
		
		daoImpl.setSessionFactory(sessionFactory);
		return daoImpl;
	}
	
	public HbmKeggDrugMetaboliteDaoImpl buildHbmKeggDrugMetaboliteDao() {
		HbmKeggDrugMetaboliteDaoImpl daoImpl = new HbmKeggDrugMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					KeggDrugMetaboliteEntity.class);
		}
		
		BiosynthHbmConnectionManager.registerSessionFactory(daoImpl, sessionFactory);
		
		daoImpl.setSessionFactory(sessionFactory);
		return daoImpl;
	}
	
	public HbmKeggReactionDaoImpl buildHbmKeggReactionDao() {
		HbmKeggReactionDaoImpl daoImpl = new HbmKeggReactionDaoImpl();
		
		if (sessionFactory == null) {
			initialize(KeggReactionEntity.class);
		}
		
		daoImpl.setSessionFactory(sessionFactory);
		return daoImpl;
	}
	
	private void initialize(Class<?> ... classes) {
		for (Class<?> clazz : classes)
			configuration.addAnnotatedClass(clazz);
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.build();
		
		this.sessionFactory = configuration.buildSessionFactory(servReg);
	}
}
