package edu.uminho.biosynth.core.data.integration.staging;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class HbmMetaboliteStagingManagerImpl implements IMetaboliteStagingManager {

	private IGenericDao dao;
	//STORE THE NULL FORMULA, INCHI, SMILE
	private MetaboliteInchiDim nullInchi = null;
	private MetaboliteSmilesDim nullSmiles = null;
	private MetaboliteFormulaDim nullFormula = null;
	
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

	@Override
	public MetaboliteXrefGroupDim createOrGetXrefGroupDim(Set<Integer> xrefIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetaboliteXrefGroupDim createOrGetNameGroupDim(Set<Integer> namesIds) {
		// TODO Auto-generated method stub
		return null;
	}

}
