package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class KeggMetaboliteStagingTransform extends AbstractMetaboliteStagingTransform<KeggCompoundMetaboliteEntity, KeggCompoundMetaboliteCrossreferenceEntity>{
	
//	private Map<Set<Integer>, Integer> xrefsToGroupId = new HashMap<> ();
//	private Map<Integer, Set<Integer>> groupToxrefsId = new HashMap<> ();
//	
//	public void generateXref() {
//		for (MetaboliteXrefGroupDim xrefGroup : dao.findAll(MetaboliteXrefGroupDim.class)) {
//			Integer groupId = xrefGroup.getId();
//			if (!groupToxrefsId.containsKey(groupId)) {
//				Set<Integer> setOfXrefIds = new HashSet<> ();
//				groupToxrefsId.put(groupId, setOfXrefIds);
//			}
////			groupToxrefsId.get(groupId).add(xrefGroup.getMetgetId());
//		}
//		
//		for (Integer groupId : groupToxrefsId.keySet()) {
//			xrefsToGroupId.put(groupToxrefsId.get(groupId), groupId);
//		}
//	}

	@Override
	public MetaboliteStga etlTransform(KeggCompoundMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
//		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setRemark( cpd.getRemark());
		cpd_stga.setComment( cpd.getComment());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteSmilesDim( this.generateSmiles(null));
		cpd_stga.setMetaboliteInchiDim( this.generateInChI(null));
		List<String> names = new ArrayList<> ();
		if (cpd.getName() != null && cpd.getName().trim().length() > 0)
		for (String name : cpd.getName().split(";")) {
			if (name.trim().length() > 0) names.add(name);
		}
		cpd_stga.setMetaboliteNameGroupDim( this.generateNames(names));
		cpd_stga.setMetaboliteXrefGroupDim( this.generateXrefGroup(cpd.getCrossreferences()));
		
		return cpd_stga;
	}
}
