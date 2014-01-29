package edu.uminho.biosynth.core.data.io.dao.bigg;

import java.io.Serializable;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class BiggHbmDaoImpl extends GenericEntityDaoImpl {

	public Serializable save(BiggMetaboliteEntity entity) {
		Serializable id = this.sessionFactory.getCurrentSession().save(entity);
		for (BiggMetaboliteCrossReferenceEntity xref : entity.getCrossReferences()) {
			this.sessionFactory.getCurrentSession().save(xref);
		}
		return id;
	}

	public Serializable[] save(BiggMetaboliteEntity... entities) {
		// TODO Auto-generated method stub
		return null;
	}
}
