package edu.uminho.biosynth.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.BiosynthHbmConnectionManager;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggCompoundMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggDrugMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggGlycanMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.RestKeggCompoundMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.RestKeggGlycanMetaboliteDaoImpl;

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
		HbmKeggGlycanMetaboliteDaoImpl daoImpl = new HbmKeggGlycanMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					KeggGlycanMetaboliteEntity.class, 
					KeggGlycanMetaboliteCrossreferenceEntity.class);
		}
		
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
