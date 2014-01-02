package edu.uminho.biosynth.core.data.service.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.AbstractGenericEntity;
import edu.uminho.biosynth.core.data.service.BiggService;
import edu.uminho.biosynth.core.data.service.BiocycService;
import edu.uminho.biosynth.core.data.service.KeggService;

public class MapperService {
	
	private final static Logger LOGGER = Logger.getLogger(MapperService.class.getName());
	
	public static<T extends AbstractGenericEntity> List<String> filterEntityEntry(List<T> entityList) {
		List<String> entryList = new ArrayList<> ();
		for (int i = 0; i < entityList.size(); i++) {
			AbstractGenericEntity entity = entityList.get(i);
			entryList.add( entity == null ? "null" : entity.getEntry());
		}
		return entryList;
	}

	public static Class<?> referenceStringToEntityClass() {
		return null;
	}
	
	public static Class<?> referenceStringToServiceClass(String string) {
		Class<?> type = null;
		switch (string) {
		case "kegg":
			type = KeggService.class;
			break;
		case "metacyc":
			type = BiocycService.class;
			break;
		case "bigg":
			type = BiggService.class;
			break;
		default:
			LOGGER.log(Level.INFO, "No mapping for -> " + string);
			break;
		}
		return type;
	}
}
