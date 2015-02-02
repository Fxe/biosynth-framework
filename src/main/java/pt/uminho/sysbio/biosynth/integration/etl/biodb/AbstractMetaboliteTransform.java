package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.EtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public abstract class AbstractMetaboliteTransform<M extends GenericMetabolite> 
implements EtlTransform<M, GraphMetaboliteEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetaboliteTransform.class);
	
	protected static final String MODEL_LABEL = GlobalLabel.MetabolicModel.toString();
	protected static final String SUPER_METABOLITE_LABEL = GlobalLabel.SuperMetabolite.toString();
	protected static final String METABOLITE_LABEL = GlobalLabel.Metabolite.toString();
	protected static final String METABOLITE_PROPERTY_LABEL = GlobalLabel.MetaboliteProperty.toString();
	
	protected static final String PROPERTY_UNIQUE_KEY = "key";
	
	protected static final String METABOLITE_FORMULA_LABEL = MetabolitePropertyLabel.MolecularFormula.toString();
	protected static final String METABOLITE_NAME_LABEL = MetabolitePropertyLabel.Name.toString();
	protected static final String METABOLITE_SMILE_LABEL = MetabolitePropertyLabel.SMILES.toString();
	protected static final String METABOLITE_MOL_FILE_LABEL = MetabolitePropertyLabel.MDLMolFile.toString();
	protected static final String METABOLITE_INCHI_LABEL = MetabolitePropertyLabel.InChI.toString();
	protected static final String METABOLITE_CHARGE_LABEL = MetabolitePropertyLabel.Charge.toString();
	
	protected static final String METABOLITE_FORMULA_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_molecular_formula.toString();
	protected static final String METABOLITE_NAME_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_name.toString();
	protected static final String METABOLITE_SMILE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_smiles.toString();
	protected static final String METABOLITE_MOL_FILE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_mdl_mol_file.toString();
	protected static final String METABOLITE_INCHI_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_inchi.toString();
	protected static final String METABOLITE_CHARGE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_charge.toString();
	
	protected static final String METABOLITE_INSTANCE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.instance_of.toString();
	
	protected static final String METABOLITE_COMPARMENT_RELATIONSHIP_TYPE = MetaboliteRelationshipType.found_in.toString();
	protected static final String METABOLITE_MODEL_RELATIONSHIP_TYPE = MetaboliteRelationshipType.included_in.toString();
	
	protected static final String METABOLITE_CROSSREFERENCE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.has_crossreference_to.toString();
	
	private final String majorLabel;
	
	protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
			new AnnotationPropertyContainerBuilder();
	protected final EtlDictionary<String, String> dictionary;
	
	public AbstractMetaboliteTransform(String majorLabel, EtlDictionary<String, String> dictionary) {
		this.majorLabel = majorLabel;
		this.dictionary = dictionary;
	}
	
	@Override
	public GraphMetaboliteEntity etlTransform(M metabolite) {
		GraphMetaboliteEntity centralMetaboliteEntity = new GraphMetaboliteEntity();

		this.configureProperties(centralMetaboliteEntity, metabolite);
		this.configureFormulaLink(centralMetaboliteEntity, metabolite);
		this.configureNameLink(centralMetaboliteEntity, metabolite);
		this.configureAdditionalPropertyLinks(centralMetaboliteEntity, metabolite);
		this.configureCrossreferences(centralMetaboliteEntity, metabolite);
		
		return centralMetaboliteEntity;
	}
	
	protected void configureProperties(GraphMetaboliteEntity centralMetaboliteEntity, M metabolite) {
		centralMetaboliteEntity.setMajorLabel(majorLabel);
		centralMetaboliteEntity.addLabel(METABOLITE_LABEL);
		centralMetaboliteEntity.addProperty("entry", metabolite.getEntry());
		centralMetaboliteEntity.addProperty("formula", metabolite.getFormula());
		centralMetaboliteEntity.addProperty("description", metabolite.getDescription());
		centralMetaboliteEntity.addProperty("metaboliteClass", metabolite.getMetaboliteClass());
		centralMetaboliteEntity.addProperty("name", metabolite.getName());
		centralMetaboliteEntity.addProperty("source", metabolite.getSource());
		
		try {
			Map<String, Object> propertyContainer = 
					this.propertyContainerBuilder.extractProperties(
							metabolite, metabolite.getClass());
			
			for (String key : propertyContainer.keySet()) {
				centralMetaboliteEntity.addProperty(key, propertyContainer.get(key));
			}
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	protected GraphPropertyEntity buildPropertyEntity(String key, Object value, String majorLabel, String...labels) {
		if (majorLabel == null) return null;
		if (key == null) return null;
		if (value == null) return null;
		if (value instanceof String) {
			String value_ = (String)value;
			if (value_.trim().isEmpty()) return null;
		}
		
		Map<String, Object> properties = new HashMap<> ();
		properties.put(key, value);
		List<String> labels_ = new ArrayList<> (Arrays.asList(labels));
		labels_.add(METABOLITE_PROPERTY_LABEL);
		return buildPropertyEntity(properties, key, majorLabel, labels_);
	}
	
	/**
	 * Everything with 2 at the end does not automatic add the METABOLITE_PROPERTY_LABEL
	 * @param key
	 * @param value
	 * @param majorLabel
	 * @param labels
	 * @return
	 */
	protected GraphPropertyEntity buildPropertyEntity2(String key, Object value, String majorLabel, String...labels) {
		if (majorLabel == null) return null;
		if (key == null) return null;
		if (value == null) return null;
		if (value instanceof String) {
			String value_ = (String)value;
			if (value_.trim().isEmpty()) return null;
		}
		
		Map<String, Object> properties = new HashMap<> ();
		properties.put(key, value);
		List<String> labels_ = Arrays.asList(labels);
		return buildPropertyEntity(properties, key, majorLabel, labels_);
	}
	
	protected GraphPropertyEntity buildPropertyEntity(Map<String, Object> properties, String unique, String majorLabel, List<String> labels) {
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(properties);
		propertyEntity.uniqueProperty = unique;
		propertyEntity.setMajorLabel(majorLabel);
		for (String label : labels) {
			propertyEntity.addLabel(label);
		}
		return propertyEntity;
	}
	
	protected GraphPropertyEntity buildPropertyEntity(
			Object value, String majorLabel) {
		return buildPropertyEntity("key", value, majorLabel);
	}
	
	protected GraphRelationshipEntity buildRelationhipEntity(
			String relationShipType) {
		GraphRelationshipEntity relationshipEntity =
				new GraphRelationshipEntity();
		relationshipEntity.setMajorLabel(relationShipType);
		
		return relationshipEntity;
	}
	
	protected Pair<GraphPropertyEntity, GraphRelationshipEntity> buildPropertyLinkPair(
			String key, Object value, String majorLabel, String relationShipType, String...labels) {
		GraphPropertyEntity propertyEntity = this.buildPropertyEntity(key, value, majorLabel, labels);
		GraphRelationshipEntity relationshipEntity = this.buildRelationhipEntity(relationShipType);
		if (propertyEntity == null || relationshipEntity == null) {
			LOGGER.debug(String.format("Ignored Property/Link %s -> %s::%s:%s", relationShipType, majorLabel, key, value));
			return null;
		}
		Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyPair =
				new ImmutablePair<>(propertyEntity, relationshipEntity);
		
		return propertyPair;
	}
	
	/**
	 * Everything with 2 at the end does not automatic add the METABOLITE_PROPERTY_LABEL
	 * @param key
	 * @param value
	 * @param majorLabel
	 * @param labels
	 * @return
	 */
	protected Pair<GraphPropertyEntity, GraphRelationshipEntity> buildPropertyLinkPair2(
			String key, Object value, String majorLabel, String relationShipType, String...labels) {
		GraphPropertyEntity propertyEntity = this.buildPropertyEntity2(key, value, majorLabel, labels);
		GraphRelationshipEntity relationshipEntity = this.buildRelationhipEntity(relationShipType);
		if (propertyEntity == null || relationshipEntity == null) {
			LOGGER.debug(String.format("Ignored Property/Link %s -> %s::%s:%s", relationShipType, majorLabel, key, value));
			return null;
		}
		Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyPair =
				new ImmutablePair<>(propertyEntity, relationshipEntity);
		
		return propertyPair;
	}
	
//	protected Pair<GraphPropertyEntity, GraphRelationshipEntity> buildPropertyLinkPair(
//			String key, Object value, String[] a, String relationShipType) {
//		GraphPropertyEntity propertyEntity = this.buildPropertyEntity(key, value, majorLabel);
//		GraphRelationshipEntity relationshipEntity = this.buildRelationhipEntity(relationShipType);
//		if (propertyEntity == null || relationshipEntity == null) {
//			LOGGER.debug(String.format("Ignored Property/Link %s -> %s::%s:%s", relationShipType, majorLabel, key, value));
//			return null;
//		}
//		Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyPair =
//				new ImmutablePair<>(propertyEntity, relationshipEntity);
//		
//		return propertyPair;
//	}
	
	protected Pair<GraphPropertyEntity, GraphRelationshipEntity> buildPropertyLinkPair(
			String key, Object value, String majorLabel, String relationShipType, Map<String, Object> relationshipProperties) {
		GraphPropertyEntity propertyEntity = this.buildPropertyEntity(key, value, majorLabel);
		GraphRelationshipEntity relationshipEntity = this.buildRelationhipEntity(relationShipType);
		relationshipEntity.setProperties(relationshipProperties);
		if (propertyEntity == null || relationshipEntity == null) {
			LOGGER.debug(String.format("Ignored Property/Link %s -> %s::%s:%s", relationShipType, majorLabel, key, value));
			return null;
		}
		Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyPair =
				new ImmutablePair<>(propertyEntity, relationshipEntity);
		
		return propertyPair;
	}
	
	protected void configureFormulaLink(GraphMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						"key", 
						entity.getFormula(), 
						METABOLITE_FORMULA_LABEL, 
						METABOLITE_FORMULA_RELATIONSHIP_TYPE));
	}
	
	protected void configureNameLink(GraphMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						"key", 
						entity.getName(), 
						METABOLITE_NAME_LABEL, 
						METABOLITE_NAME_RELATIONSHIP_TYPE));
	}
	
	protected abstract void configureAdditionalPropertyLinks(GraphMetaboliteEntity centralMetaboliteEntity, M metabolite);
	
	protected void foo(GraphMetaboliteEntity centralMetaboliteEntity, M metabolite) {
		
	}
	
	protected void configureCrossreferences(GraphMetaboliteEntity centralMetaboliteEntity, M metabolite) {
		LOGGER.debug("Setup cross-references ...");
		
		try {
			Method method = metabolite.getClass().getMethod("getCrossreferences");
			List<?> xrefs = List.class.cast(method.invoke(metabolite));
			for (Object xrefObject : xrefs) {
				LOGGER.debug("Found cross-reference: " + xrefObject);
				GenericCrossReference xref = GenericCrossReference.class.cast(xrefObject);
				switch (xref.getType()) {
					case DATABASE:
						GraphMetaboliteProxyEntity proxyEntity = new GraphMetaboliteProxyEntity();
						proxyEntity.setEntry(xref.getValue());
						proxyEntity.setMajorLabel(this.dictionary.translate(xref.getRef()));
						proxyEntity.addLabel(METABOLITE_LABEL);
						GraphRelationshipEntity graphRelationshipEntity = new GraphRelationshipEntity();
						Map<String, Object> properties = this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass());
						graphRelationshipEntity.setProperties(properties);
						graphRelationshipEntity.setMajorLabel(METABOLITE_CROSSREFERENCE_RELATIONSHIP_TYPE);
						centralMetaboliteEntity.addCrossreference(proxyEntity, graphRelationshipEntity);
						break;
					case MODEL:
						centralMetaboliteEntity.addPropertyEntity(
								this.buildPropertyLinkPair(
										"key", xref.getRef(), 
										MODEL_LABEL, 
										METABOLITE_MODEL_RELATIONSHIP_TYPE));
						break;
					default:
						LOGGER.warn(String.format("Ignored type <%s>[%s:%s]", xref.getType(), xref.getRef(), xref.getValue()));
						break;
				}
			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error(e.getMessage());
		}
	};
}
