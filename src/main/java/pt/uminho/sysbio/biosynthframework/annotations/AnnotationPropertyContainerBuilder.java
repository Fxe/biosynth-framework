package pt.uminho.sysbio.biosynthframework.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AnnotationPropertyContainerBuilder  {
	
	@SuppressWarnings("unused")
	private boolean ignoreNull = true;
	
	public Map<String, Object> omg(Object object) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> propertyContainer = new HashMap<> ();
		
//		System.out.println(object.getClass());
		
		for (Field property : object.getClass().getDeclaredFields()) {
			Annotation annotation = property.getAnnotation(MetaProperty.class);
			property.setAccessible(true);
			if (annotation != null) {
				Object value = property.get(object);
				if (value != null) propertyContainer.put(property.getName(), value);
			}
		}
		
		Class<?> superClass = object.getClass().getSuperclass();
		System.out.println(superClass);
		for (Field property : superClass.getDeclaredFields()) {
			Annotation annotation = property.getAnnotation(MetaProperty.class);
			property.setAccessible(true);
			if (annotation != null) {
				Object value = property.get(object);
				if (value != null) propertyContainer.put(property.getName(), value);
			}
		}
		
		Class<?> evenMoreSuperClass = superClass.getSuperclass();
		System.out.println(evenMoreSuperClass);
		for (Field property : evenMoreSuperClass.getDeclaredFields()) {
			Annotation annotation = property.getAnnotation(MetaProperty.class);
			property.setAccessible(true);
			if (annotation != null) {
				Object value = property.get(object);
				if (value != null) propertyContainer.put(property.getName(), value);
			}
		}
		
		Class<?> MoreMoreSuperClass = evenMoreSuperClass.getClass().getSuperclass();
		for (Field property : MoreMoreSuperClass.getDeclaredFields()) {
			Annotation annotation = property.getAnnotation(MetaProperty.class);
			property.setAccessible(true);
			if (annotation != null) {
				Object value = property.get(object);
				if (value != null) propertyContainer.put(property.getName(), value);
			}
		}
		
		return propertyContainer;
	}
}
