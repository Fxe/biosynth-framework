package pt.uminho.sysbio.biosynthframework.util;

import java.lang.reflect.Field;

import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(HibernateUtils.class);
	
	/**
	 * From stackoverflow to initialize all lazycollections
	 * http://stackoverflow.com/questions/24327353/initialize-all-lazy-loaded-collections-in-hibernate
	 * 
	 * @param tClass
	 * @param entity
	 */
	public static <T> void forceLoadLazyCollections(Class<T> tClass, Object entity) {
		if (entity == null) {
			return;
		}
		
		for (Field field : tClass.getDeclaredFields()) {
			LazyCollection annotation = field.getAnnotation(LazyCollection.class);
			
			if (annotation != null) {
				LOGGER.debug("Fetch lazy collection - " + field.getName());
				try {
					field.setAccessible(true);
					Hibernate.initialize(field.get(entity));
				} catch (IllegalAccessException e) {
					LOGGER.warn("Unable to force initialize field: " + field.getName());
				}
			}
		}
	}
}
