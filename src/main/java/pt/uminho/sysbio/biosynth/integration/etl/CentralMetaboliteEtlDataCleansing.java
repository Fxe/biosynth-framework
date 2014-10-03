package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.util.FormulaConverter;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetabolitePropertyEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteRelationshipEntity;

public class CentralMetaboliteEtlDataCleansing
implements EtlDataCleansing<CentralMetaboliteEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CentralMetaboliteEtlDataCleansing.class);
	
	private static final String DCS_STATUS_KEY = "DCS-status";
	
	private final FormulaConverter formulaConverter;
	
	public CentralMetaboliteEtlDataCleansing(FormulaConverter formulaConverter) {
		this.formulaConverter = formulaConverter;
	}
	
//	public FormulaConverter getFormulaConverter() { return formulaConverter;}
//	public void setFormulaConverter(FormulaConverter formulaConverter) { this.formulaConverter = formulaConverter;}

	private Triple<String, String, EtlCleasingType> cleanseFormula(String formula) {
		
		String formula_ = formulaConverter.convertToIsotopeMolecularFormula(formula, false);
		EtlCleasingType action = EtlCleasingType.UNCHANGED;
		if (!formula_.equals(formula)) {
			action = EtlCleasingType.CORRECTED;
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
	public Map<String, Triple<String, String, EtlCleasingType>> etlCleanse(CentralMetaboliteEntity metabolite) {
		Map<String, Triple<String, String, EtlCleasingType>> result =
				new HashMap<> ();
		
		for (Pair<CentralMetabolitePropertyEntity, CentralMetaboliteRelationshipEntity> pair : 
			metabolite.getPropertyEntities()) {
			
			CentralMetabolitePropertyEntity propertyEntity = pair.getLeft();
			CentralMetaboliteRelationshipEntity relationshipEntity = pair.getRight();
			Triple<String, String, EtlCleasingType> triple;
			
			switch (propertyEntity.getMajorLabel()) {
				case "MolecularFormula":
					triple = this.cleanseFormula((String)propertyEntity.getProperties().get("key"));
					propertyEntity.setUniqueKey(triple.getLeft());
					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight());
					result.put("ChemicalFormula", triple);
					break;
				case "Name":
					triple = this.cleanseName((String)propertyEntity.getProperties().get("key"));
					propertyEntity.setUniqueKey(triple.getLeft());
					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight());
					result.put("Name", triple);
					break;
				default:
					break;
			}
		}
		
		return result;
	}
}
