package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetabolitePropertyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import edu.uminho.biosynth.core.components.GenericMetabolite;

public abstract class AbstractMetaboliteTransform<M extends GenericMetabolite> 
implements EtlTransform<M, CentralMetaboliteEntity> {
	
	protected final static String METABOLITE_LABEL = "Metabolite";
	protected final static String METABOLITE_PROPERTY_LABEL = "MetaboliteProperty";
	protected final static String METABOLITE_FORMULA_LABEL = "Formula";
	protected final static String METABOLITE_NAME_LABEL = "Name";
	protected static final String METABOLITE_SMILE_LABEL = "SMILES";
	protected static final String METABOLITE_INCHI_LABEL = "InChI";
	
	private final String majorLabel;
	
	public AbstractMetaboliteTransform(String majorLabel) {
		this.majorLabel = majorLabel;
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
	
	protected CentralMetabolitePropertyEntity buildPropertyEntity(Object value, String majorLabel) {
		return buildPropertyEntity("key", value, majorLabel);
	}
	
	protected void configureFormulaLink(CentralMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyEntity(entity.getFormula(), METABOLITE_FORMULA_LABEL));
	}
	
	protected void configureNameLink(CentralMetaboliteEntity centralMetaboliteEntity, M entity) {
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyEntity(entity.getName(), METABOLITE_NAME_LABEL));
	}
	
	protected abstract void configureAdditionalPropertyLinks(CentralMetaboliteEntity centralMetaboliteEntity, M entity);
	
	protected abstract void configureCrossreferences(CentralMetaboliteEntity centralMetaboliteEntity, M entity);
}
