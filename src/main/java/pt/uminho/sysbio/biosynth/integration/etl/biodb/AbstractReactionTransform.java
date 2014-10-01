package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.CentralReactionEntity;
import pt.uminho.sysbio.biosynth.integration.CentralReactionProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;

public abstract class AbstractReactionTransform<R extends GenericReaction>
implements EtlTransform<R, CentralReactionEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReactionTransform.class);
	
	protected static final String REACTION_LABEL = GlobalLabel.Reaction.toString();
	protected static final String METABOLITE_LABEL = GlobalLabel.Metabolite.toString();
	protected static final String PROPERTY_UNIQUE_KEY = "key";
	
	private final String majorLabel;
	
	public AbstractReactionTransform(String majorLabel) {
		this.majorLabel = majorLabel;
	}
	
	@Override
	public CentralReactionEntity etlTransform(R entity) {
		CentralReactionEntity centralReactionEntity = new CentralReactionEntity();

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
	
	protected void configureProperties(CentralReactionEntity centralReactionEntity, R reaction) {
		centralReactionEntity.setMajorLabel(majorLabel);
		centralReactionEntity.addLabel(REACTION_LABEL);
		centralReactionEntity.putProperty("entry", reaction.getEntry());
		centralReactionEntity.putProperty("description", reaction.getDescription());
		centralReactionEntity.putProperty("name", reaction.getName());
		centralReactionEntity.putProperty("source", reaction.getSource());
	}
	
	private Map<CentralMetaboliteProxyEntity, Double> getMetabolitesByReflexion(
			R reaction, 
			String field) {
		
		Map<CentralMetaboliteProxyEntity, Double> result = new HashMap<> ();
		
		try {
			Method method = reaction.getClass().getMethod(field);
			List<?> right = List.class.cast(method.invoke(reaction));
			for (Object stoichiometryObject : right) {
				StoichiometryPair stoichiometryPair = StoichiometryPair.class.cast(stoichiometryObject);
				CentralMetaboliteProxyEntity entity = new CentralMetaboliteProxyEntity();
				entity.setEntry(String.format("%s:%s", reaction.getSource(), stoichiometryPair.getCpdEntry()));
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
	
	protected void setupLeftMetabolites(CentralReactionEntity centralReactionEntity, R reaction) {
		LOGGER.debug("Reading Left Components");
		Map<CentralMetaboliteProxyEntity, Double> left =
				this.getMetabolitesByReflexion(reaction, "getLeft");
		
		centralReactionEntity.setLeft(left);
	}
	
	protected void setupRightMetabolites(CentralReactionEntity centralReactionEntity, R reaction) {
		LOGGER.debug("Reading Right Components");
		Map<CentralMetaboliteProxyEntity, Double> right =
				this.getMetabolitesByReflexion(reaction, "getRight");
		
		centralReactionEntity.setRight(right);
	}
	
	protected abstract void configureAdditionalPropertyLinks(CentralReactionEntity centralReactionEntity, R reaction);
	
	protected  void configureCrossreferences(
			CentralReactionEntity centralReactionEntity, 
			R reaction) {
		
		try {
			Method method = reaction.getClass().getMethod("getCrossreferences");
			List<?> xrefs = List.class.cast(method.invoke(reaction));
			for (Object xrefObject : xrefs) {
				GenericCrossReference xref = GenericCrossReference.class.cast(xrefObject);
				CentralReactionProxyEntity proxyEntity = new CentralReactionProxyEntity();
				proxyEntity.setEntry(xref.getValue());
				proxyEntity.setMajorLabel(BioDbDictionary.translateDatabase(xref.getRef()));
				
				centralReactionEntity.getCrossreferences().add(proxyEntity);
			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
//	public CentralMetaboliteEntity 
}
