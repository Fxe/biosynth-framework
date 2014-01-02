package edu.uminho.biosynth.core.data.service.cascade;

import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;

public interface ICascadeStrategy {

	public<T extends GenericMetabolite> List<?> cascade(
			GenericMetabolite entity, Class<T> type, 
			Map<String, List<IMetaboliteService<? super GenericMetabolite>>> services);
}
