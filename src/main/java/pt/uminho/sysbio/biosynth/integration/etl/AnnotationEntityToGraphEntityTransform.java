package pt.uminho.sysbio.biosynth.integration.etl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynthframework.StoichiometryPair;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public class AnnotationEntityToGraphEntityTransform<R> implements EtlTransform<R, GraphReactionEntity>{

	private final static Logger LOGGER = LoggerFactory.getLogger(AnnotationEntityToGraphEntityTransform.class);
	
	private AnnotationPropertyContainerBuilder propertyContainerBuilder = 
			new AnnotationPropertyContainerBuilder();
	
	@Override
	public GraphReactionEntity etlTransform(R srcObject) {
		GraphReactionEntity centralReactionEntity = new GraphReactionEntity();
		
		try {
		
			//setup property container
			Map<String, Object> properties = propertyContainerBuilder.omg(srcObject);
			centralReactionEntity.setProperties(properties);
			//setup labels
			//build other edges
			Map<GraphMetaboliteProxyEntity, Map<String, Object>> left =
					this.getMetabolitesByReflexion(srcObject, "getLeft");
			centralReactionEntity.setLeft(left);
			Map<GraphMetaboliteProxyEntity, Map<String, Object>> right =
					this.getMetabolitesByReflexion(srcObject, "getRight");
			centralReactionEntity.setRight(right);
		} catch (Exception e) {
			LOGGER.error("Unable to transform: " + srcObject.getClass().getCanonicalName());
			centralReactionEntity = null;
		}
		
		return centralReactionEntity;
	}
	
	private Map<GraphMetaboliteProxyEntity, Map<String, Object>> getMetabolitesByReflexion(
			R reaction, 
			String field) {
		
		Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
		
		try {
			Method method = reaction.getClass().getMethod(field);
			List<?> right = List.class.cast(method.invoke(reaction));
			for (Object stoichiometryObject : right) {
				Map<String, Object> propertyContainer = this.propertyContainerBuilder.omg(stoichiometryObject);
				System.out.println(propertyContainer);
				StoichiometryPair stoichiometryPair = StoichiometryPair.class.cast(stoichiometryObject);
				GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
				entity.setEntry(stoichiometryPair.getCpdEntry());
				//NO CLUE WHY I DID THIS ! Biocyc ?
//				entity.setEntry(String.format("%s:%s", reaction.getSource(), stoichiometryPair.getCpdEntry()));
				//FIXME: BAD ASSUMPTION -> Metabolite and Reaction may have distinct labels ex.: LigandReaction -> LigandCompound
//				entity.setMajorLabel(this.majorLabel);
//				entity.addLabel(METABOLITE_LABEL);
				
				result.put(entity, propertyContainer);
			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
		
		return result;
	}

}
