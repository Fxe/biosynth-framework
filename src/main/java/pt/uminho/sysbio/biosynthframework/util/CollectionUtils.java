package pt.uminho.sysbio.biosynthframework.util;

import java.util.Collection;
import java.util.Set;

public class CollectionUtils {

	public static<C extends Collection<T>, T> Collection<T> fromArray(T[] array, Class<C> clazz) {
		try {
			Collection<T> collection = clazz.newInstance();
			for (T o : array) {
				collection.add(o);
			}
			return collection;
		} catch (Exception e) {
			return null;	
		}
	}
	
//	public static inters
	
	public static<C extends Set<T>, T> Set<T> toSet(T[] array, Class<C> clazz) {
		try {
			Set<T> set = clazz.newInstance();
			for (T o : array) {
				set.add(o);
			}
			return set;
		} catch (Exception e) {
			return null;	
		}
	}
}
