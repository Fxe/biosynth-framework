package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEcNumberEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;

public class BiocycReactionTransform
extends AbstractReactionTransform<BioCycReactionEntity> { 
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BiocycReactionTransform.class);
	
	protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
			new AnnotationPropertyContainerBuilder();
	
	public BiocycReactionTransform(String majorLabel) {
		super(majorLabel, new BiobaseMetaboliteEtlDictionary<>(BioCycMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity centralReactionEntity,
			BioCycReactionEntity reaction) {

		for (String pwy : reaction.getPathways()) {
			LOGGER.debug("Add pathway link: " + pwy);
			centralReactionEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(pwy)
							.withMajorLabel(this.majorLabel)
							.withLabel(GlobalLabel.BioCyc)
							.withLabel(GlobalLabel.MetabolicPathway)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.in_pathway)));
		}
		
		for (String rxn : reaction.getSubInstances()) {
			LOGGER.debug("Add sub reaction link: " + rxn);
			centralReactionEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(rxn)
							.withMajorLabel(this.majorLabel)
							.withLabel(GlobalLabel.BioCyc)
							.withLabel(GlobalLabel.Reaction)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.sub_instance)));
		}
		
//		for (String parent : reaction.getParents()) {
//			centralReactionEntity.addConnectedEntity(
//					this.buildPair(
//					new SomeNodeFactory()
//							.withEntry(parent)
//							.withLabel(GlobalLabel.BioCyc)
//							.buildGraphReactionProxyEntity(ReactionMajorLabel.valueOf(majorLabel)), 
//					new SomeNodeFactory().buildReactionEdge(
//							ReactionRelationshipType.instance_of)));
//		}
		
		for (BioCycReactionEcNumberEntity ecn : reaction.getEcNumbers()) {
			LOGGER.debug("Add EC number: " + ecn.getEcNumber());
			try {
				Map<String, Object> properties = propertyContainerBuilder.extractProperties(ecn, BioCycReactionEcNumberEntity.class);
				centralReactionEntity.addConnectedEntity(
						this.buildPair(
						new SomeNodeFactory()
								.withEntry(ecn.getEcNumber())
								.withMajorLabel(this.majorLabel)
								.withLabel(GlobalLabel.EnzymeCommission)
								.buildGenericNodeEntity(), 
						new SomeNodeFactory()
								.withProperties(properties)
								.buildReactionEdge(ReactionRelationshipType.has_ec_number)));
			} catch (IllegalAccessException e) {
				LOGGER.error("IllegalAccess - " + e.getMessage());
			}
		}
	}

	@Override
	protected String resolveComponentLabel(String entry) {
		return majorLabel;
	}

}
