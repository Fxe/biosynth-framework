package edu.uminho.biosynth.core.data.io.dao;

import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionRightEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionEcNumberEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionRightEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteNameEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggDrugMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggReactionRightEntity;

public class HelperHbmConfigInitializer {

	private static final Logger LOGGER = Logger.getLogger(HelperHbmConfigInitializer.class);
	
	public static SessionFactory initializeHibernateSession(String cfg) {
		Configuration config = new Configuration().configure(cfg);
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSession(File cfg) {
		Configuration config = new Configuration().configure(cfg);
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSessionAutoAnnotate(File cfg) {
		Configuration config = new Configuration().configure(cfg);
		config.addAnnotatedClass(BiggMetaboliteEntity.class)
			  .addAnnotatedClass(BiggMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(BioCycMetaboliteEntity.class)
			  .addAnnotatedClass(BioCycMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(KeggCompoundMetaboliteEntity.class)
			  .addAnnotatedClass(KeggCompoundMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(KeggGlycanMetaboliteEntity.class)
			  .addAnnotatedClass(KeggGlycanMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(KeggDrugMetaboliteEntity.class)
			  .addAnnotatedClass(KeggDrugMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(ChebiMetaboliteEntity.class)
			  .addAnnotatedClass(ChebiMetaboliteNameEntity.class)
			  .addAnnotatedClass(ChebiMetaboliteCrossreferenceEntity.class)
			  
			  .addAnnotatedClass(KeggReactionEntity.class)
			  .addAnnotatedClass(KeggReactionLeftEntity.class)
			  .addAnnotatedClass(KeggReactionRightEntity.class)
			  .addAnnotatedClass(BioCycReactionEntity.class)
			  .addAnnotatedClass(BioCycReactionCrossReferenceEntity.class)
			  .addAnnotatedClass(BioCycReactionLeftEntity.class)
			  .addAnnotatedClass(BioCycReactionRightEntity.class)
			  .addAnnotatedClass(BioCycReactionEcNumberEntity.class)
			  .addAnnotatedClass(BiggReactionEntity.class)
			  .addAnnotatedClass(BiggReactionLeftEntity.class)
			  .addAnnotatedClass(BiggReactionRightEntity.class)
			  .addAnnotatedClass(BiggReactionCrossReferenceEntity.class);
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSession(File cfg, Class<?>...classes) {
		Configuration config = new Configuration().configure(cfg);
		for (Class<?> c : classes) {
			config.addAnnotatedClass(c);
		}
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	@Deprecated
	public static SessionFactory initializeMySQLHibernateSession(
			File cfg, Class<?>...classes) {
		
		Configuration config = new Configuration().configure(cfg);
		for (Class<?> c : classes) {
			config.setProperty("", "");
			config.setProperty("", "");
			config.setProperty("", "");
			config.addAnnotatedClass(c);
		}
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
}
