package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class FixMe implements EtlTransform<BiggMetaboliteEntity, GraphMetaboliteEntity> {

	@Override
	public GraphMetaboliteEntity etlTransform(BiggMetaboliteEntity cpd) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", cpd.getId());
		properties.put("entry", cpd.getEntry());
		properties.put("name", cpd.getName());
		properties.put("formula", cpd.getFormula());
		properties.put("charge", cpd.getCharge());
		
		GraphMetaboliteEntity metaboliteEntity = new GraphMetaboliteEntity();
		metaboliteEntity.setProperties(properties);
		
//		metaboliteEntity.getPropertyEntities().add(MetabolitePropertyBuilder.buildFormula(cpd.getFormula()));
//		metaboliteEntity.getPropertyEntities().add(MetabolitePropertyBuilder.buildCharge(cpd.getCharge()));
//		metaboliteEntity.getPropertyEntities().add(MetabolitePropertyBuilder.buildName(cpd.getName()));
//		
		return metaboliteEntity;
	}

}
