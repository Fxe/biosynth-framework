package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetabolitePropertyEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.EtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericMetabolite;

public abstract class AbstractMetaboliteTransform<M extends GenericMetabolite> 
implements EtlTransform<M, CentralMetaboliteEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetaboliteTransform.class);
	
	protected static final String MODEL_LABEL = GlobalLabel.Model.toString();
	protected static final String SUPER_METABOLITE_LABEL = GlobalLabel.SuperMetabolite.toString();
	protected static final String METABOLITE_LABEL = GlobalLabel.Metabolite.toString();
	protected static final String METABOLITE_PROPERTY_LABEL = GlobalLabel.MetaboliteProperty.toString();
	
	protected static final String PROPERTY_UNIQUE_KEY = "key";
	
	protected static final String METABOLITE_FORMULA_LABEL = MetabolitePropertyLabel.MolecularFormula.toString();
	protected static final String METABOLITE_NAME_LABEL = MetabolitePropertyLabel.Name.toString();
	protected static final String METABOLITE_SMILE_LABEL = MetabolitePropertyLabel.SMILES.toString();
	protected static final String METABOLITE_INCHI_LABEL = MetabolitePropertyLabel.InChI.toString();
	protected static final String METABOLITE_CHARGE_LABEL = MetabolitePropertyLabel.Charge.toString();
	
	protected static final String METABOLITE_FORMULA_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasMolecularFormula.toString();
	protected static final String METABOLITE_NAME_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasName.toString();
	protected static final String METABOLITE_SMILE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasSMILES.toString();
	protected static final String METABOLITE_INCHI_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasInChI.toString();
	protected static final String METABOLITE_CHARGE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasCharge.toString();
	
	protected static final String METABOLITE_INSTANCE_RELATIONSHIP_TYPE = MetaboliteRelationshipType.InstanceOf.toString();
	
	protected static final String METABOLITE_MODEL_RELATIONSHIP_TYPE = MetaboliteRelationshipType.HasCharge.toString();
	
	private final String majorLabel;
	
	protected final EtlDictionary<String, String> dictionary;
	
	public AbstractMetaboliteTransform(String majorLabel, EtlDictionary<String, String> dictionary) {
		this.majorLabel = majorLabel;
		this.dictionary = dictionary;
	}
	
	@Override
	public CentralMetaboliteEntity etlTransform(M entity) {
		CentralMetaboliteEntity centralMetaboliteEntity = new CentralMetaboliteEntity();
		centralMetaboliteEntity.setMajorLabel(majorLabel);
		centralMetaboliteEntity.addLabel(METABOLITE_LABEL);
		centralMetaboliteEntity.putProperty("entry", entity.getEntry());
		centralMetaboliteEntity.putProperty("formula", entity.getFormula());
		centralMetaboliteEntity.putProperty("description", entity.getDescription());
		centralMetaboliteEntity.putProperty("metaboliteClass", entity.getMetaboliteClass());
		centralMetaboliteEntity.putProperty("name", entity.getName());
		centralMetaboliteEntity.putProperty("source", entity.getSource());
		
		this.configureFormulaLink(centralMetaboliteEntity, entity);
		this.configureNameLink(centralMetaboliteEntity, entity);
		this.configureAdditionalPropertyLinks(centralMetaboliteEntity, entity);
		this.configureCrossreferences(centralMetaboliteEntity, entity);
		
		return centralMetaboliteEntity;
	}
	
	protected CentralMetabolitePropertyEntity buildPropertyEntity(String key, Object value, String majorLabel) {
		if (majorLabel == null) return null;
		if (key == null) return null;
		if (value == null) return null;
		if (value instanceof String) {
			String value_ = (String)value;
			if (value_.trim().isEmpty()) return null;
		}
		
		
		CentralMetabolitePropertyEntity propertyEntity =
				new CentralMetabolitePropertyEntity(key, value);
		propertyEntity.setMajorLabel(majorLabel);
		propertyEntity.addLabel(METABOLITE_PROPERTY_LABEL);
		
		return propertyEntity;
	}
	
	protected CentralMetabolitePropertyEntity buildPropertyEntity(
			Object value, String majorLabel) {
		return buildPropertyEntity("key", value, majorLabel);
	}
	
	protected CentralMetaboliteRelationshipEntity buildRelationhipEntity(
			String relationShipType) {
		CentralMetaboliteRelationshipEntity relationshipEntity =
				new CentralMetaboliteRelationshipEntity();
		relationshipEntity.setMajorLabel(relationShipType);
		
		return relationshipEntity;
	}
	
	protected Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity> buildPropertyLinkPair(
			String key, Object value, String majorLabel, String relationShipType) {
		CentralMetabolitePropertyEntity propertyEntity = this.buildPropertyEntity(key, value, majorLabel);
		CentralMetaboliteRelationshipEntity relationshipEntity = this.buildRelationhipEntity(relationShipType);
		if (propertyEntity == null || relationshipEntity == null) {
			LOGGER.debug(String.format("Ignored Property/Link %s -> %s::%s:%s", relationShipType, majorLabel, key, value));
			return null;
		}
		Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity> propertyPair =
				new ImmutablePair<>(propertyEntity, relationshipEntity);
		
		return propertyPair;
	}
	
	protected void configureFormulaLink(CentralMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						"key", 
						entity.getFormula(), 
						METABOLITE_FORMULA_LABEL, 
						METABOLITE_FORMULA_RELATIONSHIP_TYPE));
	}
	
	protected void configureNameLink(CentralMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						"key", 
						entity.getName(), 
						METABOLITE_NAME_LABEL, 
						METABOLITE_NAME_RELATIONSHIP_TYPE));
	}
	
	protected abstract void configureAdditionalPropertyLinks(CentralMetaboliteEntity centralMetaboliteEntity, M metabolite);
	
	protected void configureCrossreferences(CentralMetaboliteEntity centralMetaboliteEntity, M metabolite) {
		try {
			Method method = metabolite.getClass().getMethod("getCrossreferences");
			List<?> xrefs = List.class.cast(method.invoke(metabolite));
			for (Object xrefObject : xrefs) {
				GenericCrossReference xref = GenericCrossReference.class.cast(xrefObject);
				
				switch (xref.getType()) {
					case DATABASE:
						CentralMetaboliteProxyEntity proxyEntity = new CentralMetaboliteProxyEntity();
						proxyEntity.setEntry(xref.getValue());
						proxyEntity.setMajorLabel(this.dictionary.translate(xref.getRef()));
						
						centralMetaboliteEntity.getCrossreferences().add(proxyEntity);
						break;
					case MODEL:
						centralMetaboliteEntity.addPropertyEntity(
								this.buildPropertyLinkPair(
										"key", xref.getRef(), 
										MODEL_LABEL, 
										METABOLITE_MODEL_RELATIONSHIP_TYPE));
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
	};
}
