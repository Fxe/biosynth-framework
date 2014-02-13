package edu.uminho.biosynth.core.data.integration.staging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefGroup;

public class KeggMetaboliteStageLoader extends AbstractMetaboliteStageLoader<KeggMetaboliteEntity, KeggMetaboliteCrossReferenceEntity>{
	
	private Map<Set<Integer>, Integer> xrefsToGroupId = new HashMap<> ();
	private Map<Integer, Set<Integer>> groupToxrefsId = new HashMap<> ();
	
	public void generateXref() {
		for (MetaboliteXrefGroup xrefGroup : dao.findAll(MetaboliteXrefGroup.class)) {
			Integer groupId = xrefGroup.getId();
			if (!groupToxrefsId.containsKey(groupId)) {
				Set<Integer> setOfXrefIds = new HashSet<> ();
				groupToxrefsId.put(groupId, setOfXrefIds);
			}
			groupToxrefsId.get(groupId).add(xrefGroup.getMetaboliteXrefDim().getId());
		}
		
		for (Integer groupId : groupToxrefsId.keySet()) {
			xrefsToGroupId.put(groupToxrefsId.get(groupId), groupId);
		}
	}

	@Override
	public MetaboliteStga stageMetabolite(KeggMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setRemark( cpd.getRemark());
		cpd_stga.setComment( cpd.getComment());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroup( this.generateXrefGroup(cpd.getCrossReferences()));
		
		
		return cpd_stga;
	}
}
