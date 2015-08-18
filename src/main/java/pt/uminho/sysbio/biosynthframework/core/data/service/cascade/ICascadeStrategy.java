package pt.uminho.sysbio.biosynthframework.core.data.service.cascade;

import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.core.data.service.IMetaboliteService;

public interface ICascadeStrategy {

	public<T extends GenericMetabolite> List<?> cascade(
			GenericMetabolite entity, Class<T> type, 
			Map<String, List<IMetaboliteService<? super GenericMetabolite>>> services);
}
