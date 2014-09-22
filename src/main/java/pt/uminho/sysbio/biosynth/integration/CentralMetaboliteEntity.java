package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.language.bm.Rule.RPattern;
import org.apache.commons.lang3.tuple.Pair;

import edu.uminho.biosynth.core.components.Metabolite;

public class CentralMetaboliteEntity extends AbstractCentralEntity implements Metabolite {

	private List<CentralMetaboliteProxyEntity> crossreferences = new  ArrayList<> ();
	private List<Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity>> propertyEntities = new ArrayList<> ();

	

//	
//	@Override
//	public void setFormula(String formula) {
//		this.formula = formula;
//		properties.put("formula", formula);
//	};
	
	public List<CentralMetaboliteProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<CentralMetaboliteProxyEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}
	public void addCrossreference(CentralMetaboliteProxyEntity crossreference) {
		this.crossreferences.add(crossreference);
	}

	public List<Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity>> getPropertyEntities() {
		return propertyEntities;
	}
	public void setPropertyEntities(
			List<Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity>> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	public void addPropertyEntity(Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity> propertyEntity) {
		if (propertyEntity != null) {
			if (propertyEntity.getLeft() == null || propertyEntity.getRight() == null) {
				throw new RuntimeException();
			}
			this.propertyEntities.add(propertyEntity);
		}
	}
	
	@Override
	public String getEntry() { return (String)this.properties.get("entry");}
	public void setEntry(String entry) { properties.put("entry", entry);};

	@Override
	public String getName() { return (String)this.properties.get("name");}
	@Override
	public String getFormula() { return (String)this.properties.get("formula");}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Strong Properties:\n");
		if (propertyEntities.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (Pair<?, ?> p : propertyEntities) {
				sb.append(p.getLeft().getClass().getSimpleName()).append("\n")
				  .append(p.getLeft()).append(" => \n")
				  .append(p.getRight().getClass().getSimpleName()).append("\n")
				  .append(p.getRight());
			}
		}
		sb.append("Crossreference Properties:\n");
		if (crossreferences.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralMetaboliteProxyEntity x : crossreferences) {
				sb.append(x);
			}
		}
		return sb.toString();
	}
}
