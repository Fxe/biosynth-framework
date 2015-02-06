package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.util.FormulaReader;

public class CentralMetaboliteEtlDataCleansing
implements EtlDataCleansing<GraphMetaboliteEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CentralMetaboliteEtlDataCleansing.class);
	
	private static final String DCS_STATUS_KEY = "DCS-status";
	
	private final FormulaReader formulaConverter;
	
	public CentralMetaboliteEtlDataCleansing(FormulaReader formulaConverter) {
		this.formulaConverter = formulaConverter;
	}
	
//	public FormulaConverter getFormulaConverter() { return formulaConverter;}
//	public void setFormulaConverter(FormulaConverter formulaConverter) { this.formulaConverter = formulaConverter;}

	private Triple<String, String, EtlCleasingType> cleanseFormula(String formula) {
		
		String formula_ = formulaConverter.convertToIsotopeMolecularFormula(formula, false);
		
		
		EtlCleasingType action = EtlCleasingType.UNCHANGED;
		if (formula_ != null && !formula_.equals(formula)) {
			action = EtlCleasingType.CORRECTED;
		} else if (formula_ == null) {
			formula_ = formula;
			action = EtlCleasingType.CORRUPT;
		}
		
		LOGGER.debug(String.format("%s %s -> %s", action, formula, formula_));
		
		return new ImmutableTriple<String, String, EtlCleasingType>(formula_, formula, action);
	}
	
	public Triple<String, String, EtlCleasingType> cleanseName(String name) {
		String name_ = name.toLowerCase();
		EtlCleasingType action = EtlCleasingType.UNCHANGED;
		if (!name_.equals(name)) {
			action = EtlCleasingType.CORRECTED;
		}
		
		LOGGER.debug(String.format("%s %s -> %s", action, name, name_));
		
		return new ImmutableTriple<String, String, EtlCleasingType>(name_, name, action);
	}
	
	public Triple<String, String, EtlCleasingType> cleanseSmiles(String formula) {
		return null;
	}
	
	@Override
	public Map<String, Triple<String, String, EtlCleasingType>> etlCleanse(GraphMetaboliteEntity metabolite) {
		Map<String, Triple<String, String, EtlCleasingType>> result =
				new HashMap<> ();
		
		for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : metabolite.getConnectedEntities()) {
			AbstractGraphNodeEntity propertyEntity = p.getRight();
			AbstractGraphEdgeEntity relationshipEntity = p.getLeft();
			Triple<String, String, EtlCleasingType> triple;
			switch (propertyEntity.getMajorLabel()) {
				case "MolecularFormula":
					triple = this.cleanseFormula((String)propertyEntity.getProperties().get("key"));
					propertyEntity.getProperties().put(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, nullToString(triple.getLeft()));
//					propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
					result.put("ChemicalFormula", triple);
					break;
				case "Name":
					triple = this.cleanseName((String)propertyEntity.getProperties().get("key"));
					propertyEntity.getProperties().put(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, nullToString(triple.getLeft()));
					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
					result.put("Name", triple);
					break;
				default:
					LOGGER.debug("Ignored connection: " + propertyEntity.getMajorLabel());
					break;
			}
		}
		
//		for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair : 
//			metabolite.getPropertyEntities()) {
//			
//			GraphPropertyEntity propertyEntity = pair.getLeft();
//			GraphRelationshipEntity relationshipEntity = pair.getRight();
//			Triple<String, String, EtlCleasingType> triple;
//			
//			switch (propertyEntity.getMajorLabel()) {
//				case "MolecularFormula":
//					triple = this.cleanseFormula((String)propertyEntity.getProperties().get("key"));
//					propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
//					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
//					result.put("ChemicalFormula", triple);
//					break;
//				case "Name":
//					triple = this.cleanseName((String)propertyEntity.getProperties().get("key"));
//					propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
//					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
//					result.put("Name", triple);
//					break;
//				default:
//					break;
//			}
//		}
		
		return result;
	}
	
	private Object nullToString(Object object) {
		if (object == null) return "null";
		return object;
	}
}
