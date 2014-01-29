package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

public class AbstractEntityIdGenerator extends IdentityGenerator {
	private static final Logger LOGGER = Logger.getLogger(AbstractEntityIdGenerator.class.getName());
	
	@Override
	public Serializable generate(SessionImplementor s, Object obj) {
		if (obj == null) {
			LOGGER.log(Level.SEVERE, "Id generator null object");
			throw new HibernateException(new NullPointerException());
		}
		
		Serializable id;
		if ((((AbstractGenericEntity) obj).getId()) == null) {
			id = super.generate(s, obj);
		} else {
			id = ((AbstractGenericEntity)obj).getId();
		}
		
		System.out.println(id);
		return id;
	}
}
