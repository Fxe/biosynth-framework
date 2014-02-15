package edu.uminho.biosynth.core.data.integration.staging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteNameBridge;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteNameBridgeId;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteNameGroupDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefBridge;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefBridgeId;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class HbmMetaboliteStagingManagerImpl implements IMetaboliteStagingManager {

	private IGenericDao dao;
	//STORE THE NULL FORMULA, INCHI, SMILE
	private MetaboliteInchiDim nullInchi = null;
	private MetaboliteSmilesDim nullSmiles = null;
	private MetaboliteFormulaDim nullFormula = null;
	private MetaboliteXrefGroupDim nullXrefGroup = null;
	private MetaboliteNameGroupDim nullNameGroup = null;
	
	private Map<Set<Integer>, Integer> nameSetToGroupId = new HashMap<> ();
	private Map<Integer, Set<Integer>> groupIdToNameSet = new HashMap<> ();
	
	private Map<Set<Integer>, Integer> xrefSetToGroupId = new HashMap<> ();
	private Map<Integer, Set<Integer>> groupIdToXrefSet = new HashMap<> ();
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}

	
	
	@Override
	public MetaboliteServiceDim createOrGetService(MetaboliteServiceDim service) {
		MetaboliteServiceDim res = dao.find(MetaboliteServiceDim.class, service.getServiceName());
		if (res == null) {
			res = new MetaboliteServiceDim();
			res.setServiceVersion(service.getServiceVersion());
			res.setServiceName(service.getServiceName());
			dao.save(service);
		}
		return res;
	}

	@Override
	public MetaboliteSmilesDim getNullSmilesDim() {
		if (nullSmiles == null) {
			List<MetaboliteSmilesDim> res = dao.criteria(MetaboliteSmilesDim.class, Restrictions.eq("smiles", NULL_SMILES));
			if (res.size() < 1) {
				nullSmiles = new MetaboliteSmilesDim();
				nullSmiles.setSmiles(NULL_SMILES);
				dao.save(nullSmiles);
			} else {
				//SHOULD WARNING IF MORE THAN 2
				//  ^ THIS IMPOSSIBLE SINCE SMILES IS UNIQUE !
				nullSmiles = res.iterator().next();
			}
		}
		return nullSmiles;
	}

	@Override
	public MetaboliteInchiDim getNullInchiDim() {
		if (nullInchi == null) {
			List<MetaboliteInchiDim> res = dao.criteria(MetaboliteInchiDim.class, Restrictions.eq("inchiKey", NULL_INCHI));
			if (res.size() < 1) {
				nullInchi = new MetaboliteInchiDim();
				nullInchi.setInchi(NULL_INCHI);
				nullInchi.setInchiKey(NULL_INCHI);
				dao.save(nullInchi);
			} else {
				//SHOULD WARNING IF MORE THAN 2
				//  ^ THIS IMPOSSIBLE SINCE INCHIKEY IS UNIQUE !
				nullInchi = res.iterator().next();
			}
		}
		return nullInchi;
	}

	@Override
	public MetaboliteInchiDim getInvalidInchiDim(String errorType, String longMsg) {
		MetaboliteInchiDim invalidInchi = null;
		List<MetaboliteInchiDim> res = dao.criteria(MetaboliteInchiDim.class, Restrictions.eq("inchiKey", errorType));
		if (res.size() < 1) {
			invalidInchi = new MetaboliteInchiDim();
			invalidInchi.setInchi("InChI Error " + longMsg);
			invalidInchi.setInchiKey(errorType);
			dao.save(invalidInchi);
		} else {
			//SHOULD WARNING IF MORE THAN 2
			//  ^ THIS IMPOSSIBLE SINCE INCHIKEY IS UNIQUE !
			invalidInchi = res.iterator().next();
		}
	
		return invalidInchi;
	}

	@Override
	public MetaboliteFormulaDim getNullFormulaDim() {
		if (nullFormula == null) {
			List<MetaboliteFormulaDim> res = dao.criteria(MetaboliteFormulaDim.class, Restrictions.eq("formula", NULL_FORMULA));
			if (res.size() < 1) {
				nullFormula = new MetaboliteFormulaDim();
				nullFormula.setFormula(NULL_FORMULA);
				dao.save(nullFormula);
			} else {
				//SHOULD WARNING IF MORE THAN 2
				//  ^ THIS IMPOSSIBLE SINCE FORMULA IS UNIQUE !
				nullFormula = res.iterator().next();
			}
		}
		return nullFormula;
	}
	
	private void initializeNameGroupSearch() {
		this.nameSetToGroupId.clear();
		this.groupIdToNameSet.clear();
		
		for (MetaboliteNameBridge bridge : dao.findAll(MetaboliteNameBridge.class)) {
			Integer groupId = bridge.getId().getNameGroupId();
			Integer nameId = bridge.getId().getNameId();
			if (groupIdToNameSet.containsKey(groupId)) {
				groupIdToNameSet.put(groupId, new HashSet<Integer> ());
			}
			
			groupIdToNameSet.get(groupId).add(nameId);
		}
		
		for (Integer groupId : groupIdToNameSet.keySet()) {
			if (nameSetToGroupId.put(groupIdToNameSet.get(groupId), groupId) != null) {
				//ERROR
			}
		}
	}
	
	private void initializeXrefGroupSearch() {
		this.xrefSetToGroupId.clear();
		this.groupIdToXrefSet.clear();
		
		for (MetaboliteXrefBridge bridge : dao.findAll(MetaboliteXrefBridge.class)) {
			Integer groupId = bridge.getId().getXrefGroupId();
			Integer xrefId = bridge.getId().getXrefId();
			if (groupIdToXrefSet.containsKey(groupId)) {
				groupIdToXrefSet.put(groupId, new HashSet<Integer> ());
			}
			
			groupIdToXrefSet.get(groupId).add(xrefId);
		}
		
		for (Integer groupId : groupIdToXrefSet.keySet()) {
			if (xrefSetToGroupId.put(groupIdToXrefSet.get(groupId), groupId) != null) {
				//ERROR GROUP AND SETS
			}
		}
	}

	@Override
	public MetaboliteXrefGroupDim createOrGetXrefGroupDim(Set<Integer> xrefIds) {
		MetaboliteXrefGroupDim xrefGroupDim = null;
		
		synchronized (xrefSetToGroupId) {
			if (this.xrefSetToGroupId.isEmpty()) {
				this.initializeXrefGroupSearch();
			}
			
			if (this.xrefSetToGroupId.containsKey(xrefIds)) {
				xrefGroupDim = this.dao.getReference(MetaboliteXrefGroupDim.class, this.xrefSetToGroupId.get(xrefIds));
			} else {
				xrefGroupDim = new MetaboliteXrefGroupDim();
				this.dao.save(xrefGroupDim);
				this.xrefSetToGroupId.put(xrefIds, xrefGroupDim.getId());
				for (Integer xrefId : xrefIds) {
					MetaboliteXrefBridge nameBridge = new MetaboliteXrefBridge();
					nameBridge.setId(new MetaboliteXrefBridgeId(xrefGroupDim.getId(), xrefId));
					this.dao.save(nameBridge);
				}
			}
		}
		
		return xrefGroupDim;
	}

	@Override
	public MetaboliteNameGroupDim createOrGetNameGroupDim(Set<Integer> namesIds) {
		MetaboliteNameGroupDim nameGroupDim = null;
		
		synchronized (nameSetToGroupId) {
			if (nameSetToGroupId.isEmpty()) {
				this.initializeNameGroupSearch();
			}
			
			if (nameSetToGroupId.containsKey(namesIds)) {
				nameGroupDim = dao.getReference(MetaboliteNameGroupDim.class, nameSetToGroupId.get(namesIds));
			} else {
				nameGroupDim = new MetaboliteNameGroupDim();
				dao.save(nameGroupDim);
				nameSetToGroupId.put(namesIds, nameGroupDim.getId());
				for (Integer nameId : namesIds) {
					MetaboliteNameBridge nameBridge = new MetaboliteNameBridge();
					nameBridge.setId(new MetaboliteNameBridgeId(nameId, nameGroupDim.getId()));
					dao.save(nameBridge);
				}
			}
		}
		
		return nameGroupDim;
	}
	@Override
	public MetaboliteXrefGroupDim getNullXrefGroupDim() {
		if (nullXrefGroup == null) {
			List<MetaboliteXrefGroupDim> res = dao.query(""); 
					dao.criteria(MetaboliteXrefGroupDim.class, Restrictions.eq("smiles", NULL_SMILES));
			if (res.size() < 1) {
				nullXrefGroup = new MetaboliteXrefGroupDim();
				dao.save(nullXrefGroup);
			} else {
				//SHOULD WARNING IF MORE THAN 2
				//  ^ THIS IMPOSSIBLE SINCE SMILES IS UNIQUE !
				nullXrefGroup = res.iterator().next();
			}
		}
		return nullXrefGroup;
	}
	@Override
	public MetaboliteNameGroupDim getNullNameGroupDim() {
		// TODO Auto-generated method stub
		return null;
	}

}
