package edu.uminho.biosynth.core.data.io.dao;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HelperHbmConfigInitializer {

	
	public static SessionFactory initializeHibernateSession(String cfg) {
		Configuration config = new Configuration().configure(cfg);
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSession(File cfg) {
		Configuration config = new Configuration().configure(cfg);
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
}
