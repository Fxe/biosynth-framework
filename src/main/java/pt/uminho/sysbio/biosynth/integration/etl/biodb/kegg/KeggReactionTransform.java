package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionPropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionRightEntity;

public class KeggReactionTransform 
extends AbstractReactionTransform<KeggReactionEntity>{

	private static final Logger LOGGER = LoggerFactory.getLogger(KeggReactionTransform.class);
	private static final String KEGG_REACTION_LABEL = ReactionMajorLabel.LigandReaction.toString();
	private static final String KEGG_COMPOUND_METABOLITE_LABEL = MetaboliteMajorLabel.LigandCompound.toString();
	private static final String KEGG_GLYCAN_METABOLITE_LABEL = MetaboliteMajorLabel.LigandGlycan.toString();
	
	public KeggReactionTransform() { super(KEGG_REACTION_LABEL);}

	@Override
	protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, KeggReactionEntity entity) {
		for(KeggReactionLeftEntity left : entity.getLeft()) {
			GraphMetaboliteProxyEntity proxyEntity = new GraphMetaboliteProxyEntity();
			
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
			
			Map<String, Object> propertyMap = null; 
			try {
				propertyMap = this.propertyContainerBuilder.extractProperties(left, left.getClass());
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				propertyMap = new HashMap<> ();
				propertyMap.put("stoichiometry", left.getStoichiometry());
			}
			
			centralReactionEntity.getLeft().put(proxyEntity, propertyMap);
		}
	}
	
	@Override
	protected void setupRightMetabolites(
			GraphReactionEntity centralReactionEntity,
			KeggReactionEntity reaction) {
		
		for(KeggReactionRightEntity right : reaction.getRight()) {
			GraphMetaboliteProxyEntity proxyEntity = new GraphMetaboliteProxyEntity();
			
			String entry = right.getCpdEntry();
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
			
			Map<String, Object> propertyMap = null; 
			try {
				propertyMap = this.propertyContainerBuilder.extractProperties(right, right.getClass());
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				propertyMap = new HashMap<> ();
				propertyMap.put("stoichiometry", right.getStoichiometry());
			}
			
			centralReactionEntity.getRight().put(proxyEntity, propertyMap);
		}
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity centralReactionEntity,
			KeggReactionEntity reaction) {
		
		LOGGER.debug("Building additional property links ...");
		
		for (String pwy : reaction.getPathways()) {
			LOGGER.debug("Add pahtway link: " + pwy);
			centralReactionEntity.getConnectedEntities().add(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(pwy)
							.withMajorLabel(GlobalLabel.KeggPathway)
							.withLabel(GlobalLabel.KEGG)
							.withLabel(GlobalLabel.MetabolicPathway)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.in_pathway)));
			
//			Map<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> link = new HashMap<> ();
//			AbstractGraphEdgeEntity edge = buildSomeEdge(null, ReactionRelationshipType.in_pathway.toString());
//			Map<String, Object> properties = new HashMap<> ();
//			properties.put("entry", pwy);
//			AbstractGraphNodeEntity node = buildSomeNode(properties, 
//					GlobalLabel.KeggPathway.toString(), 
//					GlobalLabel.KEGG.toString(),
//					GlobalLabel.MetabolicPathway.toString());
//			node.uniqueKey = "entry";
//			link.put(edge, node);
//			centralReactionEntity.links.add(link);
		}
		
		for (String ko : reaction.getOrthologies()) {
			LOGGER.debug("Add pahtway orthology: " + ko);
			centralReactionEntity.getConnectedEntities().add(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(ko)
							.withMajorLabel(GlobalLabel.KeggOrthology)
							.withLabel(GlobalLabel.KEGG)
							.withLabel(GlobalLabel.Orthology)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.in_pathway)));
//			Map<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> link = new HashMap<> ();
//			AbstractGraphEdgeEntity edge = buildSomeEdge(null, ReactionRelationshipType.has_orthology.toString());
//			Map<String, Object> properties = new HashMap<> ();
//			properties.put("entry", ko);
//			AbstractGraphNodeEntity node = buildSomeNode(properties, 
//					GlobalLabel.KeggOrthology.toString(), 
//					GlobalLabel.KEGG.toString(),
//					GlobalLabel.Orthology.toString());
//			node.uniqueKey = "entry";
//			link.put(edge, node);
//			centralReactionEntity.links.add(link);
		}
		
		for (String ecn : reaction.getEnzymes()) {
			LOGGER.debug("Add EC number: " + ecn);
			centralReactionEntity.getConnectedEntities().add(
					this.buildPair(
					new SomeNodeFactory()
							.withMajorLabel(GlobalLabel.KeggOrthology)
							.withLabel(GlobalLabel.KEGG)
							.withLabel(GlobalLabel.Orthology)
							.buildGraphReactionPropertyEntity(ReactionPropertyLabel.EnzymeCommission, ecn), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.has_ec_number)));
//			LOGGER.debug(ecn);
//			Map<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> link = new HashMap<> ();
//			AbstractGraphEdgeEntity edge = buildSomeEdge(null, ReactionRelationshipType.has_ec_number.toString());
//			Map<String, Object> properties = new HashMap<> ();
//			properties.put(PROPERTY_UNIQUE_KEY, ecn);
//			AbstractGraphNodeEntity node = buildSomeNode(properties, 
//					null, 
//					GlobalLabel.EnzymeCommission.toString());
//			node.uniqueKey = PROPERTY_UNIQUE_KEY;
//			link.put(edge, node);
//			centralReactionEntity.links.add(link);
		}
		
	}
	
	@Override
	protected void configureNameLink(GraphReactionEntity centralReactionEntity, KeggReactionEntity entity) {
		for (String name : entity.getNames()) {
			this.configureNameLink(centralReactionEntity, name);
		}
	};

	@Override
	protected void configureCrossreferences(
			GraphReactionEntity centralReactionEntity,
			KeggReactionEntity reaction) {
		
		LOGGER.debug("Ligand Reaction does not support cross-references");
	}
}
