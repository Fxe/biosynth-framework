package edu.uminho.biosynth.core.data.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.MnxReactionEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionProductEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionReactantEntity;
import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;

public class MnxService {
	
	private GenericEntityDAO dao;
	
	public MnxService(GenericEntityDAO dao) {
		this.dao = dao;
	}
	
	public MnxMetaboliteEntity getMnxMetabolite(int id) {
		return this.dao.find(MnxMetaboliteEntity.class, id);
	}
	public MnxMetaboliteEntity getMnxMetabolite(String entry) {
		List<MnxMetaboliteEntity> res = 
				this.dao.criteria(MnxMetaboliteEntity.class, Restrictions.eq("ENTRY", entry));
		if (res.size() > 0) {
			return res.get(0);
		}
		
		return null;
	}
	
	public List<MnxReactionCrossReferenceEntity> getReactionCrossreferences(String db, String value) {
		return this.dao.criteria(MnxReactionCrossReferenceEntity.class, Restrictions.and(
				Restrictions.eq("ref", db),
				Restrictions.eq("value", value)));
	}
	
	public MnxReactionEntity getMnxReaction(int id) {
		return this.dao.find(MnxReactionEntity.class, id);
	}
	public MnxReactionEntity getMnxReaction(String entry) {
		List<MnxReactionEntity> res = 
				this.dao.criteria(MnxReactionEntity.class, Restrictions.eq("ENTRY", entry));
		if (res.size() > 0) {
			return res.get(0);
		}
		
		return null;
	}
	
	public MnxMetaboliteEntity getMnxMetaboliteFromCrossReference(String value) {
		int cpdId = this.dao.criteria(MnxMetaboliteCrossReferenceEntity.class, Restrictions.eq("value", value)).get(0).getMnxMetaboliteEntity().getId();
		
		return this.dao.find(MnxMetaboliteEntity.class, cpdId);
	}
	
	public List<String> consumes(String cpdEntry) {
//		System.out.println("consumers of: " + cpd);
		List<String> consumers = new ArrayList<> ();
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MnxReactionReactantEntity.class)
//				.add(Restrictions.eq("cpdEntry", cpd));
		List<MnxReactionReactantEntity> result = dao.criteria(MnxReactionReactantEntity.class, Restrictions.eq("cpdEntry", cpdEntry));
		//(List<MnxReactionReactantEntity>) criteria.list();
		for (MnxReactionReactantEntity reactant : result) {
			consumers.add(reactant.getMnxReactionEntity().getEntry());
		}
		return consumers;
	}
	public List<String> consumes(int cpdId) {
		List<String> consumers = new ArrayList<> ();
		List<MnxReactionReactantEntity> result = 
				dao.criteria(MnxReactionReactantEntity.class, Restrictions.eq("cpdId", cpdId));
		
		for (MnxReactionReactantEntity reactant : result) {
			consumers.add(reactant.getMnxReactionEntity().getEntry());
		}
		return consumers;
	}
	
	public List<String> produces(String cpdEntry) {
		List<String> producers = new ArrayList<> ();

		List<MnxReactionProductEntity> result = 
				dao.criteria(MnxReactionProductEntity.class, Restrictions.eq("cpdEntry", cpdEntry));
//				(List<MnxReactionProductEntity>) criteria.list();
		for (MnxReactionProductEntity product : result) {
			producers.add(product.getMnxReactionEntity().getEntry());
		}
		return producers;
	}
	
	public List<String> numberOfProductsByEntry(int n) {
		List<String> producers = new ArrayList<> ();
		
		List<Object[]> omg = 
				dao.query("SELECT count(*), a.mnxReactionEntity.entry FROM "
							+ "MnxReactionProductEntity a GROUP BY a.mnxReactionEntity"); 
		for (Object[] o : omg) {
			if ((long) o[0] == n) producers.add((String) o[1]);
		}
		
		return producers;
	}
	
	public List<String> numberOfConsumersByEntry(int n) {
		List<String> consumers = new ArrayList<> ();
		
		List<Object[]> omg = 
				dao.query("SELECT count(*), a.mnxReactionEntity.entry FROM "
							+ "MnxReactionReactantEntity a GROUP BY a.mnxReactionEntity"); 
		for (Object[] o : omg) {
			if ((long) o[0] == n) consumers.add((String) o[1]);
		}
		
		return consumers;
	}
	
	public List<String> findReactionByReactantsAndProducts(List<String> reactants, List<String> products) {
		List<String> reactants_ = new ArrayList<> (reactants);
		List<String> products_ = new ArrayList<> (products);
		
		List<String> reactionsWithNReactants = numberOfConsumersByEntry(reactants_.size());
		List<String> reactionsWithNProducts = numberOfProductsByEntry(products_.size());
		
		List<String> consumers = new ArrayList<> ();
		
		if (reactants_.size() > 0) {
			consumers.addAll(consumes(reactants_.remove(0)));
			for (String reactantId : reactants_) {
				consumers.retainAll( consumes(reactantId));
			}
		}
//		System.out.println(consumers);
		consumers.retainAll(reactionsWithNReactants);
//		System.out.println(consumers);
		
		List<String> producers = new ArrayList<> ();
		
		if (products_.size() > 0) {
			producers.addAll(produces(products_.remove(0)));
			for (String productId : products_) {
				producers.retainAll( produces(productId));
			}
		}
//		System.out.println(producers);
		producers.retainAll(reactionsWithNProducts);
//		System.out.println(producers);
		
		List<String> finalAnswer = new ArrayList<> (consumers);
		finalAnswer.retainAll(producers);
		
		return finalAnswer;
	}
	
	public List<MnxReactionEntity> getReactionByCrossreference(String xrefValue) {
		List<MnxReactionEntity> result = new ArrayList<> ();
		List<MnxReactionCrossReferenceEntity> xrefs = this.dao.criteria(MnxReactionCrossReferenceEntity.class, Restrictions.eq("value", xrefValue));
		for (MnxReactionCrossReferenceEntity xref: xrefs) {
			result.add(xref.getMnxReactionEntity());
		}
		
		return result;
	}
}
