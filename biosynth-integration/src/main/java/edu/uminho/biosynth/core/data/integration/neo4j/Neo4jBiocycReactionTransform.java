package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionPropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEcNumberEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionRightEntity;

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
				result = ReactionMajorLabel.MetaCyc.toString();
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
		entity.addLabel(GlobalLabel.Reaction.toString());
		entity.addLabel(GlobalLabel.BioCyc.toString());
		
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
							ReactionRelationshipType.has_ec_number, 
							ReactionPropertyLabel.ECNumber);
			entity.getReactionProperties().add(ecnProp);
		}
		
		for (String pathway : rxn.getPathways()) {
			CentralDataReactionProperty pathwayProp = 
					this.buildSimpleProperty("entry", pathway, 
							ReactionRelationshipType.in_pathway, 
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
							leftEntity.getStoichiometry(), 
							ReactionRelationshipType.left_component.toString());
			entity.getReactionStoichiometryProperties().add(dataReactionProperty);
		}
		
		for (BioCycReactionRightEntity rightEntity : rxn.getRight()) {
			CentralDataReactionProperty dataReactionProperty = 
					this.buildStoichiometryProperty(
							rightEntity.getCpdEntry(), 
							rightEntity.getCoefficient(), 
							rxn.getSource(),
							rightEntity.getStoichiometry(), 
							ReactionRelationshipType.right_component.toString());
			
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

  @Override
  public CentralDataReactionEntity apply(BioCycReactionEntity t) {
    return etlTransform(t);
  }
}
