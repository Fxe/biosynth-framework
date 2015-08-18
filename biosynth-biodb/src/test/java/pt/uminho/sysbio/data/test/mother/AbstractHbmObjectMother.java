package pt.uminho.sysbio.data.test.mother;

import org.hibernate.Session;

/**
 * Abstract Mother Template for instancing.
 * 
 * @author Filipe Liu
 *
 * @param <T> Object Class
 */
public abstract class AbstractHbmObjectMother<T> {
	
	private final Session session;
	
	public AbstractHbmObjectMother(Session session) {
		this.session = session;
	}
	
	public T instance() {
		T object = this.loadInstance(session);
		
		if (object == null) object = this.createInstance();
		
		this.configure(object);
		this.session.save(object);
		
		return object;
	}
	
	public T instanceNonPersist() {
		T object = this.createInstance();
		this.configure(object);
		
		return object;
	}
	
	protected abstract T loadInstance(Session session);
	
	protected abstract T createInstance();
	
	protected abstract void configure(T object);
}
