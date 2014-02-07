package edu.uminho.biosynth.core.test.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestConfig {
	
	public static SessionFactory sessionFactory;
	public static IGenericDao dao;
	public static String HIBERNATE_CFG = "hibernate_production_pgsql.cfg.xml";
	
	public static void initializeHibernateSession() {
		Configuration config = new Configuration().configure(HIBERNATE_CFG);
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
		dao = new GenericEntityDaoImpl(sessionFactory);
	}
	
	public static void closeHibernateSession() {
		sessionFactory.close();
	}
}
