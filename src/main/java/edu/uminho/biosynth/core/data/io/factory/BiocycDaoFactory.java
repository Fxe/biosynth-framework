package edu.uminho.biosynth.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.BiosynthHbmConnectionManager;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.HbmBioCycMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.RestBiocycMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.RestBiocycReactionDaoImpl;

public class BiocycDaoFactory {
	
	private SessionFactory sessionFactory;
	private Configuration configuration;
	
	private String pathwayGenomeDatabase = "META";
	private String localStorage = "./";
	private boolean useLocalStorage = false;
	private boolean saveLocalStorage = false;
	
	public BiocycDaoFactory usingLocalStorage() {
		this.useLocalStorage = true;
		return this;
	}
	public BiocycDaoFactory savingToLocalStorage() {
		this.saveLocalStorage = true;
		return this;
	}
	public BiocycDaoFactory withLocalStorage(String localStorage) {
		this.localStorage = localStorage;
		return this;
	}
	public BiocycDaoFactory withPathwayGenomeDatabase(String pathwayGenomeDatabase) {
		this.pathwayGenomeDatabase = pathwayGenomeDatabase;
		return this;
	}
	
	public BiocycDaoFactory withHibernateConfigurationFile(File file) {
		configuration = new Configuration().configure(file);
		return this;
	}
	
	public RestBiocycMetaboliteDaoImpl buildRestBiocycMetaboliteDao() {
		RestBiocycMetaboliteDaoImpl daoImpl = new RestBiocycMetaboliteDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setPgdb(pathwayGenomeDatabase);
		
		return daoImpl;
	}
	
	public RestBiocycReactionDaoImpl buildRestBiocycReactionDao() {
		RestBiocycReactionDaoImpl daoImpl = new RestBiocycReactionDaoImpl();
		
		daoImpl.setLocalStorage(localStorage);
		daoImpl.setSaveLocalStorage(saveLocalStorage);
		daoImpl.setUseLocalStorage(useLocalStorage);
		daoImpl.setPgdb(pathwayGenomeDatabase);
		
		return daoImpl;
	}
	
	public HbmBioCycMetaboliteDaoImpl buildHbmBioCycMetaboliteDao() {
		HbmBioCycMetaboliteDaoImpl daoImpl = new HbmBioCycMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					BioCycMetaboliteEntity.class,
					BioCycMetaboliteCrossReferenceEntity.class);
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
