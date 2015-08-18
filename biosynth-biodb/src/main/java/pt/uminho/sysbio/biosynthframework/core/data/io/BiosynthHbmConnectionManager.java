package pt.uminho.sysbio.biosynthframework.core.data.io;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

/**
 * A total fail attempt to create a connection manager.
 * 
 * @author Filipe Liu
 *
 */
public class BiosynthHbmConnectionManager {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BiosynthHbmConnectionManager.class);

	private static Map<MetaboliteDao<?>, String> a = new HashMap<> ();
	private static Map<String, Session> sessionMap = new HashMap<> ();
	private static Map<String, SessionFactory> sessionFactoryMap = new HashMap<> ();
	private static Map<String, Transaction> transactionMap = new HashMap<> ();

	//Use some unique id in the future ... 
	//this class is mostly useless for production anyway
	private static Long id = 1L;
	
	public static SessionFactory getSessionFactory(MetaboliteDao<?> dao) {
		String id = a.get(dao);
		
		return sessionFactoryMap.get(id);
	}
	
	public static void registerSessionFactory(MetaboliteDao<?> dao, SessionFactory sessionFactory) {
		a.put(dao, id.toString());
		sessionFactoryMap.put(id.toString(), sessionFactory);
		id++;
	}
	
	public static Session openSession(MetaboliteDao<?> dao) {
		String id = a.get(dao);
		Session session = sessionMap.get(id);
		
		if (session == null) {
			session = sessionFactoryMap.get(id).getCurrentSession();
			LOGGER.info(String.format("New session[%s] opened for dao[%s]", session, dao));
			sessionMap.put(id, session);
		}
		
		return session;
	}
	
	public static Transaction beginTransaction(MetaboliteDao<?> dao) {
		String id = a.get(dao);
		Transaction transaction = transactionMap.get(id);
		
		Session session = sessionMap.get(id);
		if (session == null) {
			session = openSession(dao);
		}
		
		
		transaction = session.beginTransaction();
		LOGGER.info(String.format("New transaction[%s] opened for dao[%s]", transaction, dao));
		transactionMap.put(id, transaction);
		
		
		return transaction;
	}
	
	public static void commit(MetaboliteDao<?> dao) {
		String id = a.get(dao);
		Transaction transaction = transactionMap.get(id);
		if (transaction != null) transaction.commit();
	}
	
	public static void shutdown() {
		for (Session session : sessionMap.values()) {
			LOGGER.info(String.format("Closing session[%s]", session));
			if (session.isOpen()) session.close();
		}
		for (SessionFactory sessionFactory : sessionFactoryMap.values()) {
			LOGGER.info(String.format("Closing session_factory[%s]", sessionFactory));
			sessionFactory.close();
		}
	}
}
