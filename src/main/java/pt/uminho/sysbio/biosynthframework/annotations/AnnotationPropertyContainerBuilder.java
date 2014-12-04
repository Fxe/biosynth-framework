package pt.uminho.sysbio.biosynthframework.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationPropertyContainerBuilder  {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AnnotationPropertyContainerBuilder.class);
	
	@SuppressWarnings("unused")
	private boolean ignoreNull = true;
	
	public void extractEntityMetaProperties(Object object, Class<? extends Object> clazz) 
			throws IllegalArgumentException, IllegalAccessException {
		LOGGER.debug(String.format("Reflect: %s", clazz));
		while (!clazz.equals(Object.class)) {
			
			for (Field property : clazz.getDeclaredFields()) {
				Annotation annotation = property.getAnnotation(EntityMetaProperty.class);
				property.setAccessible(true);
				if (annotation != null) {
					
				}
			}
			
			clazz = clazz.getSuperclass();
			LOGGER.debug(String.format("Reflect: %s", clazz));
		}
	}
	
	public Map<String, Object> extractProperties(Object object, Class<? extends Object> clazz) 
			throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> propertyContainer = new HashMap<> ();
		
		LOGGER.debug(String.format("Reflect: %s", clazz));
		
		while (!clazz.equals(Object.class)) {
			for (Field property : clazz.getDeclaredFields()) {
				Annotation annotation = property.getAnnotation(MetaProperty.class);
				property.setAccessible(true);
				String key = property.getName();
				if (annotation != null) {
					Object value = property.get(object);
					if (value != null) {
						LOGGER.debug(String.format("Property - %s:%s[%s]", key, value, value.getClass().getSimpleName()));
						if (annotation instanceof MetaProperty && 
								((MetaProperty)annotation).asString()) {
							propertyContainer.put(key, value.toString());
						} else {
							propertyContainer.put(key, value);
						}
					}
				}
			}
			
			clazz = clazz.getSuperclass();
			LOGGER.debug(String.format("Reflect: %s", clazz));
		}
//		System.out.println(clazz);
//		System.out.println(object.getClass());
//		
//		for (Field property : clazz.getDeclaredFields()) {
//			Annotation annotation = property.getAnnotation(MetaProperty.class);
//			property.setAccessible(true);
//			String key = property.getName();
//			if (annotation != null) {
//				Object value = property.get(object);
//				LOGGER.debug(String.format("Property - %s:%s", key, value));
//				if (value != null) {
//					propertyContainer.put(key, value);
//				}
//			}
//		}
//		
//		Class<?> superClass = clazz.getClass().getSuperclass();
//		System.out.println(superClass);
//		for (Field property : superClass.getDeclaredFields()) {
//			Annotation annotation = property.getAnnotation(MetaProperty.class);
//			property.setAccessible(true);
//			String key = property.getName();
//			if (annotation != null) {
//				Object value = property.get(object);
//				LOGGER.debug(String.format("Property - %s:%s", key, value));
//				if (value != null) {
//					propertyContainer.put(key, value);
//				}
//			}
//		}
//		
//		Class<?> evenMoreSuperClass = superClass.getSuperclass();
//		System.out.println(evenMoreSuperClass);
//		for (Field property : evenMoreSuperClass.getDeclaredFields()) {
//			Annotation annotation = property.getAnnotation(MetaProperty.class);
//			property.setAccessible(true);
//			String key = property.getName();
//			if (annotation != null) {
//				Object value = property.get(object);
//				LOGGER.debug(String.format("Property - %s:%s", key, value));
//				if (value != null) {
//					propertyContainer.put(key, value);
//				}
//			}
//		}
//		
//		Class<?> MoreMoreSuperClass = evenMoreSuperClass.getClass().getSuperclass();
//		for (Field property : MoreMoreSuperClass.getDeclaredFields()) {
//			Annotation annotation = property.getAnnotation(MetaProperty.class);
//			property.setAccessible(true);
//			String key = property.getName();
//			if (annotation != null) {
//				Object value = property.get(object);
//				LOGGER.debug(String.format("Property - %s:%s", key, value));
//				if (value != null) {
//					propertyContainer.put(key, value);
//				}
//			}
//		}
		
		return propertyContainer;
	}
	
	public Map<String, Object> omg(Object object) throws IllegalArgumentException, IllegalAccessException {
		return this.extractProperties(object, object.getClass());
	}
}
