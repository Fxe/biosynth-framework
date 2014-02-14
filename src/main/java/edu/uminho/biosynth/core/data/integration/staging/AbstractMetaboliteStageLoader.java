package edu.uminho.biosynth.core.data.integration.staging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteNameDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteNameGroupDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public abstract class AbstractMetaboliteStageLoader<T extends GenericMetabolite, X extends GenericCrossReference> implements IMetaboliteStageLoader<T> {
	
	protected IGenericDao dao;
	protected IReferenceTransformer<X> transformer;
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	public IReferenceTransformer<X> getTransformer() { return transformer;}
	public void setTransformer(IReferenceTransformer<X> transformer) {this.transformer = transformer;}
	
	//STORE THE NULL FORMULA, INCHI, SMILE
	private MetaboliteInchiDim nullInchi = null;
	private MetaboliteSmilesDim nullSmiles = null;
	private MetaboliteFormulaDim nullFormula = null;
	
	
	protected MetaboliteFormulaDim generateFormula(String formula) {
		if (formula == null || formula.replaceAll("\\s+", "").trim().length() < 1) {
			return nullFormula;
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
			return nullInchi;
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
			dao.save(inchi_);
		}
		
		return inchi_;
	}
	
	protected MetaboliteSmilesDim generateSmiles(String smiles) {
		if (smiles == null || smiles.replaceAll("\\s+", "").trim().length() < 1) {
			return nullSmiles;
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
		MetaboliteNameGroupDim nameGroup = null;
		
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
		}
		
		return nameGroup;
	}
	
	protected MetaboliteXrefGroupDim generateXrefGroup(List<X> xrefs) {
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
				dao.save(xref_dim);
			}
			xref_.add(xref_dim);
			
			xrefIdSet.add(xref_dim.getId());
		}
		
		
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
