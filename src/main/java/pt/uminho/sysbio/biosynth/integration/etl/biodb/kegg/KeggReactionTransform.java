package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.CentralReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggReactionLeftEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.ReactionNodeLabel;

public class KeggReactionTransform 
extends AbstractReactionTransform<KeggReactionEntity>{

	private static final String KEGG_REACTION_LABEL = ReactionNodeLabel.LigandReaction.toString();
	private static final String KEGG_COMPOUND_METABOLITE_LABEL = MetaboliteMajorLabel.LigandCompound.toString();
	private static final String KEGG_GLYCAN_METABOLITE_LABEL = MetaboliteMajorLabel.LigandGlycan.toString();
	
	public KeggReactionTransform() { super(KEGG_REACTION_LABEL);}

	@Override
	protected void setupLeftMetabolites(CentralReactionEntity centralReactionEntity, KeggReactionEntity entity) {
		for(KeggReactionLeftEntity left : entity.getLeft()) {
			CentralMetaboliteProxyEntity proxyEntity = new CentralMetaboliteProxyEntity();
			
			String entry = left.getCpdEntry();
			proxyEntity.setEntry(entry);
			proxyEntity.addLabel("Metabolite");
			switch (entry.charAt(0)) {
				case 'C':
					proxyEntity.setMajorLabel(KEGG_COMPOUND_METABOLITE_LABEL);
					break;
				case 'G':
					proxyEntity.setMajorLabel(KEGG_GLYCAN_METABOLITE_LABEL);
					break;
				default:
					break;
			}
			
			centralReactionEntity.getLeft().put(proxyEntity, left.getValue());
		}
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralReactionEntity centralReactionEntity,
			KeggReactionEntity reaction) {
//		centralMetaboliteEntity.putProperty("", value);
		
	}

	@Override
	protected void configureCrossreferences(
			CentralReactionEntity centralReactionEntity,
			KeggReactionEntity reaction) {
		// TODO Auto-generated method stub
		
	}
	
//	private void addIfNotNull(String propertie, Object value, Map<String, Object> properties) {
//		if (value != null) {
//			properties.put(propertie, value);
//		}
//	}
	
//	@Override
//	public CentralDataReactionEntity etlTransform(KeggReactionEntity cpd) {
//		CentralDataReactionEntity entity = new CentralDataReactionEntity();
//		
//		entity.setMajorLabel(ReactionNodeLabel.LigandReaction.toString());
//		
//		entity.addLabel(ReactionNodeLabel.Reaction.toString());
//		entity.addLabel(ReactionNodeLabel.KEGG.toString());
//		entity.addLabel(ReactionNodeLabel.LigandReaction.toString());
//		
//		entity.setEntry(cpd.getEntry());
//		Map<String, Object> properties = new HashMap<> ();
//		
//		this.addIfNotNull("name", cpd.getName(), properties);
//		this.addIfNotNull("comment", cpd.getComment(), properties);
//		this.addIfNotNull("remark", cpd.getRemark(), properties);
//		this.addIfNotNull("definition", cpd.getDefinition(), properties);
//		this.addIfNotNull("equation", cpd.getEquation(), properties);
//		
//		
//		CentralDataReactionProperty nameProp = 
//				this.buildSimpleProperty("name", cpd.getName(), 
//						ReactionRelationshipType.HasName, 
//						ReactionPropertyLabel.Name, ReactionPropertyLabel.Reaction);
//		
//		entity.getReactionProperties().add(nameProp);
//		
//		for (String ecnumber : cpd.getEnzymes()) {
//			CentralDataReactionProperty ecnProp = 
//					this.buildSimpleProperty("ecn", ecnumber, 
//							ReactionRelationshipType.HasECNumber, 
//							ReactionPropertyLabel.ECNumber);
//			entity.getReactionProperties().add(ecnProp);
//		}
//		
//		for (String pathway : cpd.getPathways()) {
//			CentralDataReactionProperty pathwayProp = 
//					this.buildSimpleProperty("entry", pathway, 
//							ReactionRelationshipType.InPathway, 
//							ReactionPropertyLabel.Pathway);
//			entity.getReactionProperties().add(pathwayProp);
//		}
//		
//		for (String orthology : cpd.getOrthologies()) {
//			CentralDataReactionProperty orthologyProp = 
//					this.buildSimpleProperty("entry", orthology, 
//							ReactionRelationshipType.InOrthology, 
//							ReactionPropertyLabel.Orthology);
//			entity.getReactionProperties().add(orthologyProp);
//		}
//		
//		for (KeggReactionLeftEntity leftEntity : cpd.getLeft()) {
//			CentralDataReactionProperty dataReactionProperty = 
//					this.buildStoichiometryProperty(
//							leftEntity.getCpdEntry(), 
//							leftEntity.getCoefficient(), 
//							leftEntity.getValue(), 
//							ReactionRelationshipType.Left.toString());
//			entity.getReactionStoichiometryProperties().add(dataReactionProperty);
//		}
//		
//		for (KeggReactionRightEntity rightEntity : cpd.getRight()) {
//			CentralDataReactionProperty dataReactionProperty = 
//					this.buildStoichiometryProperty(
//							rightEntity.getCpdEntry(), 
//							rightEntity.getCoefficient(), 
//							rightEntity.getValue(), 
//							ReactionRelationshipType.Right.toString());
//			
//			entity.getReactionStoichiometryProperties().add(dataReactionProperty);
//		}
//		
//		entity.setProperties(properties);
//		
//		return entity;
//	}
//	
//	private CentralDataReactionProperty buildSimpleProperty(
//			String key, Object value, 
//			ReactionRelationshipType relationship, 
//			ReactionPropertyLabel label, 
//			ReactionPropertyLabel...labels) {
//		
//		CentralDataReactionProperty simpleProperty = new CentralDataReactionProperty();
//		simpleProperty.setMajorLabel(label.toString());
//		for (ReactionPropertyLabel l : labels) simpleProperty.addLabel(l.toString());
//		
//		simpleProperty.setUniqueKey(key);
//		simpleProperty.setUniqueKeyValue(value);
//		simpleProperty.setRelationshipMajorLabel(relationship.toString());
////		simpleProperty.addRelationshipLabel(relationship.toString());
//		
//		return simpleProperty;
//	}
//	
//	private CentralDataReactionProperty buildStoichiometryProperty(
//			String entry, String coefficient, Double value, String position) {
//		CentralDataReactionProperty dataReactionProperty = new CentralDataReactionProperty();
//		Map<String, Object> relationshipProperties = new HashMap<> ();
//		relationshipProperties.put("coefficient", coefficient);
//		relationshipProperties.put("value", value);
//		dataReactionProperty.setRelationshipProperties(relationshipProperties);
////		dataReactionProperty.addRelationshipLabel(ReactionRelationshipType.Stoichiometry.toString());
//		dataReactionProperty.setRelationshipMajorLabel(position);
////		dataReactionProperty.addRelationshipLabel(position);
//		
//		dataReactionProperty.addLabel(CompoundNodeLabel.Compound.toString());
//		dataReactionProperty.addLabel(CompoundNodeLabel.KEGG.toString());
//		char type = entry.charAt(0);
//		switch (type) {
//			case 'C':
//				dataReactionProperty.setMajorLabel(CompoundNodeLabel.LigandCompound.toString());
//				break;
//			case 'G':
//				dataReactionProperty.setMajorLabel(CompoundNodeLabel.LigandGlycan.toString());
//				break;
//			case 'D':
//				dataReactionProperty.setMajorLabel(CompoundNodeLabel.LigandDrug.toString());
//				break;
//			default:
//				throw new RuntimeException("Unknown stoichiometry entry " + entry);
//		}
//		
//		dataReactionProperty.setUniqueKey("entry");
//		dataReactionProperty.setUniqueKeyValue(entry);
//		
//		return dataReactionProperty;
//	}
}
