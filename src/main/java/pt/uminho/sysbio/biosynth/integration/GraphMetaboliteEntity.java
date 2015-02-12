package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class GraphMetaboliteEntity extends AbstractGraphNodeEntity implements Metabolite {

	private static final long serialVersionUID = 1L;
	
	@Deprecated
	private List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> crossreferences = new  ArrayList<> ();
	@Deprecated
	private List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> propertyEntities = new ArrayList<> ();

//	
//	@Override
//	public void setFormula(String formula) {
//		this.formula = formula;
//		properties.put("formula", formula);
//	};
	
	@Deprecated
	public void addCrossreference(Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> crossreferencePair) {
		this.crossreferences.add(crossreferencePair);
	}
	@Deprecated
	public void addCrossreference(GraphMetaboliteProxyEntity proxyEntity, GraphRelationshipEntity relationshipEntity) {
		Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> xrefPair = new ImmutablePair<>(proxyEntity, relationshipEntity);
		this.crossreferences.add(xrefPair);
	}
	@Deprecated
	public List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> getCrossreferences() {
		return crossreferences;
	}
	@Deprecated
	public void setCrossreferences(
			List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> crossreferences) {
		this.crossreferences = crossreferences;
	}
	@Deprecated
	public List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> getPropertyEntities() {
		return propertyEntities;
	}
	@Deprecated
	public void setPropertyEntities(
			List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	@Deprecated
	public void addPropertyEntity(Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyEntity) {
		if (propertyEntity != null) {
			if (propertyEntity.getLeft() == null || propertyEntity.getRight() == null) {
				throw new RuntimeException();
			}
			this.propertyEntities.add(propertyEntity);
		}
	}
	
	@Override
	public String getFormula() { return (String)this.properties.get("formula");}
	public void setFormula(String formula) { this.properties.put("formula", formula);}
	
	public Boolean isProxy() {
		if (!this.properties.containsKey("proxy")) return null;
		return (boolean) this.properties.get("proxy");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		System.out.println(this.labels);
		System.out.println(this.connectedEntities);
		return sb.toString();
	}
}
