package pt.uminho.sysbio.data.test.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class SessionFactoryRule implements MethodRule {

	private List<Class<?>> annotatedClasses = new ArrayList<> ();
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	public SessionFactoryRule(Class<?> ... classes) {
		annotatedClasses.addAll(Arrays.asList(classes));
	}
	
	public Session getSession() { return session;}
	
	public SessionFactory getSessionFactory() { return this.sessionFactory; }
	
	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				
				initialize();
				createSession();
				beginTransaction();
				
				try {
					base.evaluate();
				} finally {
					shutdown();
				}
			}
		};
	}
	
	public Session createSession() {
		this.session = this.sessionFactory.getCurrentSession();
		return this.session;
	}
	
	public void commit() {
		this.transaction.commit();
	}
	
	public Transaction beginTransaction() {
		this.transaction = this.session.beginTransaction();
		return this.transaction;
	}
	
	private void initialize() {
		Configuration configuration = new Configuration();
		
		//Add annotated classes to configuration
		for (Class<?> clazz : this.annotatedClasses)
			configuration.addAnnotatedClass(clazz);
		
		//Initialize H2 in memory database for testing
		configuration
			.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
			.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
			.setProperty("hibernate.connection.url", "jdbc:h2:mem:")
			.setProperty("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext")
			.setProperty("hibernate.hbm2ddl.auto", "create");
		
		ServiceRegistry serviceRegistry = 
				new StandardServiceRegistryBuilder().applySettings(
						configuration.getProperties()).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	private void shutdown() {
		transaction.rollback();
		if (session.isOpen()) session.close();
		sessionFactory.close();
	}

}
