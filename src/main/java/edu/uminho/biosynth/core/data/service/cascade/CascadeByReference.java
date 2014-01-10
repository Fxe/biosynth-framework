package edu.uminho.biosynth.core.data.service.cascade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;
import edu.uminho.biosynth.core.data.service.mapping.MapperService;

public class CascadeByReference implements ICascadeStrategy {

	private final static Logger LOGGER = Logger.getLogger(CascadeByReference.class.getName());
	Map<String, List<IMetaboliteService<? super GenericMetabolite>>> services;
	
	@Override
	public <T extends GenericMetabolite> List<?> cascade(
			GenericMetabolite entity, Class<T> type,
			Map<String, List<IMetaboliteService<? super GenericMetabolite>>> services) {
		
		this.services = services;
		
		List<GenericMetabolite> result = new ArrayList<> ();
		switch (type.getName()) {
		case "edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity": {
			LOGGER.log(Level.INFO, entity.getEntry() + " type of " + type.getName());
			KeggMetaboliteEntity cpd = (KeggMetaboliteEntity) entity;
			for (KeggMetaboliteCrossReferenceEntity crossReference : cpd.getCrossReferences()) {
				result.addAll( this.lookupReference(crossReference));
			}
		}
			break;
		case "edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity": {
			LOGGER.log(Level.INFO, entity.getEntry() + " type of " + type.getName());
			MnxMetaboliteEntity cpd = (MnxMetaboliteEntity) entity;
			for (MnxMetaboliteCrossReferenceEntity crossReference : cpd.getCrossReferences()) {
				result.addAll( this.lookupReference(crossReference));
			}
		}
			break;
		default:
			LOGGER.log(Level.SEVERE, "Unsupported Class " + type.getName());
			break;
		}
		
		return result;
	}
	
	private List<GenericMetabolite> lookupReference(GenericCrossReference reference) {
		List<GenericMetabolite> result = new ArrayList<> ();
		Class<?> refServiceClass = MapperService.referenceStringToServiceClass(reference.getRef());
		if (refServiceClass != null && services.containsKey(refServiceClass.getName())) {
			System.out.println("Lookup for " + refServiceClass.getName());
			for (IMetaboliteService<? super GenericMetabolite> service : services.get(refServiceClass.getName())) {
				//FIXME: I am adding nulls ! help !
				result.add(service.getMetaboliteByEntry(reference.getValue()));
			}
		}
		
		return result;
	}

}
