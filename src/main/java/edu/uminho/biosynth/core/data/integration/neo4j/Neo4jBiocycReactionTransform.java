package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionEcNumberEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionRightEntity;
import edu.uminho.biosynth.integration.etl.EtlTransform;

public class Neo4jBiocycReactionTransform implements EtlTransform<BioCycReactionEntity, CentralDataReactionEntity>{

	private void addIfNotNull(String propertie, Object value, Map<String, Object> properties) {
		if (value != null) {
			properties.put(propertie, value);
		}
	}
	
	private String sourceToFunnyLabel(String source) {
		String result = null;
		switch (source) {
			case "META":
				result = ReactionNodeLabel.MetaCyc.toString();
				break;
			default:
				break;
		}
		
		return result;
	}
	
	@Override
	public CentralDataReactionEntity etlTransform(BioCycReactionEntity rxn) {
		CentralDataReactionEntity entity = new CentralDataReactionEntity();
		
		entity.setMajorLabel(sourceToFunnyLabel(rxn.getSource()));
		
		entity.addLabel(entity.getMajorLabel());
		entity.addLabel(ReactionNodeLabel.Reaction.toString());
		entity.addLabel(ReactionNodeLabel.BioCyc.toString());
		
		entity.setEntry(rxn.getEntry());
		Map<String, Object> properties = new HashMap<> ();
		
//		this.addIfNotNull("name", rxn.getName(), properties);
		this.addIfNotNull("physiologically-relevant", rxn.getPhysiologicallyRelevant(), properties);
		this.addIfNotNull("orphan", rxn.getOrphan(), properties);
		this.addIfNotNull("reaction-direction", rxn.getReactionDirection(), properties);
		this.addIfNotNull("gibbs", rxn.getGibbs(), properties);

//		
//		
//		CentralDataReactionProperty nameProp = 
//				this.buildSimpleProperty("name", cpd.getName(), 
//						ReactionRelationshipType.HasName, 
//						ReactionPropertyLabel.Name, ReactionPropertyLabel.Reaction);
//		
//		entity.getReactionProperties().add(nameProp);
//		
		for (BioCycReactionEcNumberEntity ecnumber : rxn.getEcNumbers()) {
			CentralDataReactionProperty ecnProp = 
					this.buildSimpleProperty("ecn", ecnumber.getEcNumber(), 
							ReactionRelationshipType.HasECNumber, 
							ReactionPropertyLabel.ECNumber);
			entity.getReactionProperties().add(ecnProp);
		}
		
		for (String pathway : rxn.getPathways()) {
			CentralDataReactionProperty pathwayProp = 
					this.buildSimpleProperty("entry", pathway, 
							ReactionRelationshipType.InPathway, 
							ReactionPropertyLabel.Pathway);
			entity.getReactionProperties().add(pathwayProp);
		}
		
		for (String eznRxn : rxn.getEnzymaticReactions()) {
			CentralDataReactionProperty eznRxnProp = 
					this.buildSimpleProperty("entry", eznRxn, 
							ReactionRelationshipType.InEnzymaticReaction, 
							ReactionPropertyLabel.EnzymaticReaction);
			entity.getReactionProperties().add(eznRxnProp);
		}

		for (BioCycReactionLeftEntity leftEntity : rxn.getLeft()) {
			CentralDataReactionProperty dataReactionProperty = 
					this.buildStoichiometryProperty(
							leftEntity.getCpdEntry(), 
							leftEntity.getCoefficient(), 
							rxn.getSource(),
							leftEntity.getValue(), 
							ReactionRelationshipType.Left.toString());
			entity.getReactionStoichiometryProperties().add(dataReactionProperty);
		}
		
		for (BioCycReactionRightEntity rightEntity : rxn.getRight()) {
			CentralDataReactionProperty dataReactionProperty = 
					this.buildStoichiometryProperty(
							rightEntity.getCpdEntry(), 
							rightEntity.getCoefficient(), 
							rxn.getSource(),
							rightEntity.getValue(), 
							ReactionRelationshipType.Right.toString());
			
			entity.getReactionStoichiometryProperties().add(dataReactionProperty);
		}
		
		entity.setProperties(properties);
		
		return entity;
	}
	
	private CentralDataReactionProperty buildSimpleProperty(
			String key, Object value, 
			ReactionRelationshipType relationship, 
			ReactionPropertyLabel label, 
			ReactionPropertyLabel...labels) {
		
		CentralDataReactionProperty simpleProperty = new CentralDataReactionProperty();
		simpleProperty.setMajorLabel(label.toString());
		for (ReactionPropertyLabel l : labels) simpleProperty.addLabel(l.toString());
		
		simpleProperty.setUniqueKey(key);
		simpleProperty.setUniqueKeyValue(value);
		simpleProperty.setRelationshipMajorLabel(relationship.toString());
//		simpleProperty.addRelationshipLabel(relationship.toString());
		
		return simpleProperty;
	}
	
	private CentralDataReactionProperty buildStoichiometryProperty(
			String entry, String coefficient, String source, Double value, String position) {
		CentralDataReactionProperty dataReactionProperty = new CentralDataReactionProperty();
		Map<String, Object> relationshipProperties = new HashMap<> ();
		relationshipProperties.put("coefficient", coefficient);
		relationshipProperties.put("value", value);
		dataReactionProperty.setRelationshipProperties(relationshipProperties);
//		dataReactionProperty.addRelationshipLabel(ReactionRelationshipType.Stoichiometry.toString());
		dataReactionProperty.setRelationshipMajorLabel(position);
//		dataReactionProperty.addRelationshipLabel(position);
		
		dataReactionProperty.setMajorLabel(this.sourceToFunnyLabel(source));
		dataReactionProperty.addLabel(CompoundNodeLabel.Compound.toString());
		dataReactionProperty.addLabel(CompoundNodeLabel.BioCyc.toString());
		
		dataReactionProperty.setUniqueKey("entry");
		dataReactionProperty.setUniqueKeyValue(entry);
		
		return dataReactionProperty;
	}
}
