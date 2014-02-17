package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiOutputKey;
import net.sf.jniinchi.JniInchiWrapper;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.etl.staging.IMetaboliteStagingManager;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameGroupDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public abstract class AbstractMetaboliteStagingTransform<T extends GenericMetabolite, X extends GenericCrossReference> implements IMetaboliteStagingTransform<T> {
	
	protected IGenericDao dao;
	protected IReferenceTransformer<X> transformer;
	protected IMetaboliteStagingManager manager;
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	public IMetaboliteStagingManager getManager() {return manager; }
	public void setManager(IMetaboliteStagingManager manager) {this.manager = manager; }
	
	public IReferenceTransformer<X> getTransformer() { return transformer;}
	public void setTransformer(IReferenceTransformer<X> transformer) {this.transformer = transformer;}	
	
	protected MetaboliteFormulaDim generateFormula(String formula) {
		if (formula == null || formula.replaceAll("\\s+", "").trim().length() < 1) {
			return manager.getNullFormulaDim();
		}
		
		MetaboliteFormulaDim formula_ = null;
		for (MetaboliteFormulaDim formula_dim : dao.criteria(MetaboliteFormulaDim.class, Restrictions.eq("formula", formula))) {
			if (formula_dim.getFormula().equals(formula)) {
				formula_ = formula_dim;
			}
		}
		
		if (formula_ == null) {
			formula_ = new MetaboliteFormulaDim();
			formula_.setFormula(formula);
			dao.save(formula_);
		}
		
		return formula_;
	}
	
	protected MetaboliteInchiDim generateInChI(String inchi) {
		if (inchi == null || inchi.replaceAll("\\s+", "").trim().length() < 1) {
			return manager.getNullInchiDim();
		}
		if (!inchi.startsWith("InChI=")) {
			inchi = "InChI=".concat(inchi);
		}
		
		MetaboliteInchiDim inchi_ = null;
		for (MetaboliteInchiDim inchi_dim : dao.criteria(MetaboliteInchiDim.class, Restrictions.eq("inchi", inchi))) {
			if (inchi_dim.getInchi().equals(inchi)) {
				inchi_ = inchi_dim;
			}
		}
		
		if (inchi_ == null) {
			inchi_ = new MetaboliteInchiDim();
			inchi_.setInchi(inchi);
			JniInchiOutputKey out;
			String inchiKey = null;
			try {
				out = JniInchiWrapper.getInchiKey(inchi);
				switch (out.getReturnStatus()) {
					case OK:
						inchiKey = out.getKey();
						break;
					default:
						//RETURN INVALID INCHI
						return manager.getInvalidInchiDim(out.getReturnStatus().toString(), out.getReturnStatus().toString());
				}
			} catch (JniInchiException e) {
				//RETURN ERROR STATE INCHI
				return manager.getInvalidInchiDim("EXCEPTION", e.getMessage());
			}
			inchi_.setInchiKey(inchiKey);
			dao.save(inchi_);
		}
		
		return inchi_;
	}
	
	protected MetaboliteSmilesDim generateSmiles(String smiles) {
		if (smiles == null || smiles.replaceAll("\\s+", "").trim().length() < 1) {
			return manager.getNullSmilesDim();
		}
		
		MetaboliteSmilesDim smiles_ = null;
		for (MetaboliteSmilesDim smiles_dim : dao.criteria(MetaboliteSmilesDim.class, Restrictions.eq("smiles", smiles))) {
			if (smiles_dim.getSmiles().equals(smiles)) {
				smiles_ = smiles_dim;
			}
		}
		
		if (smiles_ == null) {
			smiles_ = new MetaboliteSmilesDim();
			smiles_.setSmiles(smiles);
			dao.save(smiles_);
		}
		
		return smiles_;
	}
	
	protected MetaboliteNameGroupDim generateNames(List<String> names) {
		System.out.println("NAMES: " + names);
		if (names.isEmpty()) return this.manager.getNullNameGroupDim();
		MetaboliteNameGroupDim nameGroup = null;
		
		Set<Integer> nameIdSet = new HashSet<> ();
		for (String name : names) {
			MetaboliteNameDim name_dim = null;
			
			for (MetaboliteNameDim query_res : dao.criteria(MetaboliteNameDim.class, Restrictions.eq("name", name))) {
				if (query_res.getName().equals(name)) {
					name_dim = query_res;
				}
			}
			
			if (name_dim == null) {
				name_dim = new MetaboliteNameDim();
				name_dim.setName(name);
				name_dim.setIupac(false);
				
				dao.save(name_dim);
			}
			
			nameIdSet.add(name_dim.getId());
		}
		
		nameGroup = this.manager.createOrGetNameGroupDim(nameIdSet);
		
		return nameGroup;
	}
	
	protected MetaboliteXrefGroupDim generateXrefGroup(List<X> xrefs) {
		System.out.println("XREFS: " + xrefs);
		if (xrefs.isEmpty()) return this.manager.getNullXrefGroupDim();
		MetaboliteXrefGroupDim xrefGroup = null;
		
		List<MetaboliteXrefDim> xref_ = new ArrayList<> ();
		Set<Integer> xrefIdSet = new HashSet<> ();
		
		//assemble xrefs either they exists from staging area or generate a new record
		for (X xref : xrefs) {
			GenericCrossReference xref_gen = transformer.transform(xref);
			MetaboliteXrefDim xref_dim = null;
			for (MetaboliteXrefDim query_res : dao.criteria(MetaboliteXrefDim.class,
					Restrictions.and(Restrictions.eq("source", xref_gen.getRef()), Restrictions.eq("value", xref_gen.getValue()))
					)) {
				if (query_res.getSource().equals(xref_gen.getRef())
						 && query_res.getValue().equals(xref_gen.getValue())) {
					xref_dim = query_res;
				}
			}
			
			if (xref_dim == null) {
				xref_dim = new MetaboliteXrefDim();
				xref_dim.setSource(xref_gen.getRef());
				xref_dim.setValue(xref_gen.getValue());
				xref_dim.setType(xref.getType().toString());
				dao.save(xref_dim);
			}
			xref_.add(xref_dim);
			
			xrefIdSet.add(xref_dim.getId());
		}
		
		xrefGroup = this.manager.createOrGetXrefGroupDim(xrefIdSet);
		//find a group with all the xrefs that were assembled
		//if group was found then return the group otherwise generate new group
//		if (xrefsToGroupId.containsKey(xrefIdSet)) {
//			xrefGroup = dao.find(MetaboliteXrefGroup.class, xrefsToGroupId.get(xrefIdSet));
//		} else {
//			xrefGroup = new MetaboliteXrefGroup();
//			for (MetaboliteXrefDim xref_dim : xref_) {
//				xrefGroup.setMetaboliteXrefDim(xref_dim);
//			}
//			dao.save(xrefGroup);
//			xrefsToGroupId.put(xrefIdSet, xrefGroup.getId());
//		}
		
		
		
		return xrefGroup;
	}
}
