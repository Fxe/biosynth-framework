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
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteXrefGroup;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public abstract class AbstractMetaboliteStageLoader<T extends GenericMetabolite, X extends GenericCrossReference> implements IMetaboliteStageLoader<T> {
	
	protected IGenericDao dao;
	protected IReferenceTransformer<X> transformer;
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	public IReferenceTransformer<X> getTransformer() { return transformer;}
	public void setTransformer(IReferenceTransformer<X> transformer) {this.transformer = transformer;}
	
	protected MetaboliteFormulaDim generateFormula(String formula) {
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
	
	protected MetaboliteXrefGroup generateXrefGroup(List<X> xrefs) {
		MetaboliteXrefGroup xrefGroup = null;
		
		List<MetaboliteXrefDim> xref_ = new ArrayList<> ();
		Set<Integer> xrefIdSet = new HashSet<> ();
		
		//assemble xrefs either they exists from staging area or generate a new record
		for (X xref : xrefs) {
			GenericCrossReference xref_gen = transformer.transform(xref);
			MetaboliteXrefDim xref_dim = null;
			for (MetaboliteXrefDim xref_dim_query : dao.criteria(MetaboliteXrefDim.class,
					Restrictions.and(Restrictions.eq("source", xref_gen.getRef()), Restrictions.eq("value", xref_gen.getValue()))
					)) {
				if (xref_dim_query.getSource().equals(xref_gen.getRef())
						 && xref_dim_query.getValue().equals(xref_gen.getValue())) {
					xref_dim = xref_dim_query;
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
