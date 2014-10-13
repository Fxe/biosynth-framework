package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.StoichiometryPair;

public abstract class AbstractReactionTransform<R extends GenericReaction>
implements EtlTransform<R, GraphReactionEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReactionTransform.class);
	
	protected static final String REACTION_LABEL = GlobalLabel.Reaction.toString();
	protected static final String METABOLITE_LABEL = GlobalLabel.Metabolite.toString();
	protected static final String PROPERTY_UNIQUE_KEY = "key";
	
	private final String majorLabel;
	
	public AbstractReactionTransform(String majorLabel) {
		this.majorLabel = majorLabel;
	}
	
	@Override
	public GraphReactionEntity etlTransform(R entity) {
		GraphReactionEntity centralReactionEntity = new GraphReactionEntity();

		this.configureProperties(centralReactionEntity, entity);
		this.setupLeftMetabolites(centralReactionEntity, entity);
		this.setupRightMetabolites(centralReactionEntity, entity);
		this.configureCrossreferences(centralReactionEntity, entity);
		
//		entity.get
//		Map<M, Double> m = entity.getLeft();
		//setup reactants
		//setup products
		//setup enzymes
		//setup pathways
		
		return centralReactionEntity;
	}
	
	protected void configureProperties(GraphReactionEntity centralReactionEntity, R reaction) {
		centralReactionEntity.setMajorLabel(majorLabel);
		centralReactionEntity.addLabel(REACTION_LABEL);
		centralReactionEntity.addProperty("entry", reaction.getEntry());
		centralReactionEntity.addProperty("description", reaction.getDescription());
		centralReactionEntity.addProperty("name", reaction.getName());
		centralReactionEntity.addProperty("source", reaction.getSource());
	}
	
	private Map<GraphMetaboliteProxyEntity, Double> getMetabolitesByReflexion(
			R reaction, 
			String field) {
		
		Map<GraphMetaboliteProxyEntity, Double> result = new HashMap<> ();
		
		try {
			Method method = reaction.getClass().getMethod(field);
			List<?> right = List.class.cast(method.invoke(reaction));
			for (Object stoichiometryObject : right) {
				StoichiometryPair stoichiometryPair = StoichiometryPair.class.cast(stoichiometryObject);
				GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
				entity.setEntry(stoichiometryPair.getCpdEntry());
				//NO CLUE WHY I DID THIS ! Biocyc ?
//				entity.setEntry(String.format("%s:%s", reaction.getSource(), stoichiometryPair.getCpdEntry()));
				//FIXME: BAD ASSUMPTION -> Metabolite and Reaction may have distinct labels ex.: LigandReaction -> LigandCompound
				entity.setMajorLabel(this.majorLabel);
				entity.addLabel(METABOLITE_LABEL);
				
				result.put(entity, stoichiometryPair.getValue());
			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
		
		return result;
	}
	
	protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, R reaction) {
		LOGGER.debug("Reading Left Components");
		Map<GraphMetaboliteProxyEntity, Double> left =
				this.getMetabolitesByReflexion(reaction, "getLeft");
		
		centralReactionEntity.setLeft(left);
	}
	
	protected void setupRightMetabolites(GraphReactionEntity centralReactionEntity, R reaction) {
		LOGGER.debug("Reading Right Components");
		Map<GraphMetaboliteProxyEntity, Double> right =
				this.getMetabolitesByReflexion(reaction, "getRight");
		
		centralReactionEntity.setRight(right);
	}
	
	protected abstract void configureAdditionalPropertyLinks(GraphReactionEntity centralReactionEntity, R reaction);
	
	protected  void configureCrossreferences(
			GraphReactionEntity centralReactionEntity, 
			R reaction) {
		
		try {
			Method method = reaction.getClass().getMethod("getCrossreferences");
			List<?> xrefs = List.class.cast(method.invoke(reaction));
			for (Object xrefObject : xrefs) {
				GenericCrossReference xref = GenericCrossReference.class.cast(xrefObject);
				
				switch (xref.getType()) {
					case DATABASE:
						GraphReactionProxyEntity proxyEntity = new GraphReactionProxyEntity();
						proxyEntity.setEntry(xref.getValue());
						proxyEntity.setMajorLabel(BioDbDictionary.translateDatabase(xref.getRef()));
						centralReactionEntity.getCrossreferences().add(proxyEntity);
						break;
					case GENE:
						break;
					default:
						break;
				}
				

			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
//	public CentralMetaboliteEntity 
}
