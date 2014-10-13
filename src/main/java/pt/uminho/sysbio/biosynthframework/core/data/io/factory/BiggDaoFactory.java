package pt.uminho.sysbio.biosynthframework.core.data.io.factory;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.BiggEquationParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.BiggReactionParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.HbmBiggMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.HbmBiggReactionDaoImpl;

public class BiggDaoFactory {
	
	private Resource resource;
	private SessionFactory sessionFactory;
	private Configuration configuration;
	private BiggEquationParser biggEquationParser;
	private BiggReactionParser biggReactionParser;
	
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
	
	public BiggDaoFactory withBiggEquationParser(BiggEquationParser biggEquationParser) {
		this.biggEquationParser = biggEquationParser;
		return this;
	}
	
	public BiggDaoFactory withBiggReactionParser(BiggReactionParser biggReactionParser) {
		this.biggReactionParser = biggReactionParser;
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
		daoImpl.setBiggEquationParser(biggEquationParser);
		daoImpl.setBiggReactionParser(biggReactionParser);
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
	
	public HbmBiggReactionDaoImpl buildHbmBiggReactionDao() {
		HbmBiggReactionDaoImpl daoImpl = new HbmBiggReactionDaoImpl();
		
		if (sessionFactory == null) {
			initialize(
					BiggReactionEntity.class, 
					BiggReactionCrossReferenceEntity.class);
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
