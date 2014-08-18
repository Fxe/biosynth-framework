package edu.uminho.biosynth.core.data.io.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao {
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	protected Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
}
