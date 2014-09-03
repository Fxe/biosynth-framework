package edu.uminho.biosynth.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.bigg.CsvBiggReactionDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.bigg.HbmBiggMetaboliteDaoImpl;

public class BiggDaoFactory {
	
	private Resource resource;
	private SessionFactory sessionFactory;
	private Configuration configuration;
	
	public BiggDaoFactory withFile(File file) {
		this.resource = new FileSystemResource(file);
		return this;
	}
	
	public BiggDaoFactory withSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}
	
	public BiggDaoFactory withHibernateConfigurationFile(File file) {
		configuration = new Configuration().configure(file);
		return this;
	}
	
	public CsvBiggMetaboliteDaoImpl buildCsvBiggMetaboliteDao() {
		CsvBiggMetaboliteDaoImpl daoImpl = new CsvBiggMetaboliteDaoImpl();
		if (!resource.exists()) {
			System.out.println(":(:(:(:(:(:(:):(:(");
		}
		daoImpl.setCsvFile(resource);
		return daoImpl;
	}
	
	public CsvBiggReactionDaoImpl buildCsvBiggReactionDao() {
		CsvBiggReactionDaoImpl daoImpl = new CsvBiggReactionDaoImpl();
		daoImpl.setCsvFile(resource);
		return daoImpl;
	}
	
	public HbmBiggMetaboliteDaoImpl buildHbmBiggMetaboliteDao() {
		HbmBiggMetaboliteDaoImpl daoImpl = new HbmBiggMetaboliteDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					BiggMetaboliteEntity.class, 
					BiggMetaboliteCrossreferenceEntity.class);
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
