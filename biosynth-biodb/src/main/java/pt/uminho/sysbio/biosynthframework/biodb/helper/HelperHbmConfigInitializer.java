package pt.uminho.sysbio.biosynthframework.biodb.helper;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionRightEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEcNumberEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionRightEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionRightEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionProductEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionReactantEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteCueEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetabolitePkEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteStructureEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionCueEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionReagentEntity;

public class HelperHbmConfigInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelperHbmConfigInitializer.class);
	
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
			  .addAnnotatedClass(SeedMetaboliteEntity.class)
			  .addAnnotatedClass(SeedMetaboliteStructureEntity.class)
			  .addAnnotatedClass(SeedMetabolitePkEntity.class)
			  .addAnnotatedClass(SeedMetaboliteCueEntity.class)
			  .addAnnotatedClass(SeedMetaboliteCrossreferenceEntity.class)
			  
			  .addAnnotatedClass(MnxMetaboliteEntity.class)
			  .addAnnotatedClass(MnxMetaboliteCrossreferenceEntity.class)
			  .addAnnotatedClass(MnxReactionEntity.class)
			  .addAnnotatedClass(MnxReactionCrossReferenceEntity.class)
			  .addAnnotatedClass(MnxReactionProductEntity.class)
			  .addAnnotatedClass(MnxReactionReactantEntity.class)
			  
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
			  .addAnnotatedClass(BiggReactionCrossReferenceEntity.class)
			  .addAnnotatedClass(SeedReactionEntity.class)
			  .addAnnotatedClass(SeedReactionCrossreferenceEntity.class)
			  .addAnnotatedClass(SeedReactionCueEntity.class)
			  .addAnnotatedClass(SeedReactionReagentEntity.class);
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
