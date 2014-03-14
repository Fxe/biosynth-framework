package edu.uminho.biosynth.core.data.io.scripts;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggDrugMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;

public class ExampleLoadAllKeggDrugsToHbm {
	
	public static void main(String args[]) {
		SessionFactory sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		HbmKeggDrugMetaboliteDaoImpl hbmDao = new HbmKeggDrugMetaboliteDaoImpl();
		RestKeggDrugMetaboliteDaoImpl restDao = new RestKeggDrugMetaboliteDaoImpl();
		restDao.setLocalStorage("D:/home/data/kegg/");
		restDao.setSaveLocalStorage(true);
		restDao.setUseLocalStorage(true);
		hbmDao.setSessionFactory(sessionFactory);
		
		sessionFactory.openSession();
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		List<Serializable> idSet = hbmDao.getAllMetaboliteIds();
		
		int i = 0;
		for (Serializable id: restDao.getAllMetaboliteIds()) {
			if (!idSet.contains(id)) {
				KeggDrugMetaboliteEntity cpd = restDao.getMetaboliteInformation(id);
				hbmDao.save(cpd);
				
				if (i % 10 == 0) {
					System.out.println(i);
					tx.commit();
					tx = sessionFactory.getCurrentSession().beginTransaction();
				}
				
				i++;
			}
//			System.out.println(cpd);
		}
		
		tx.commit();
		
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
	}
}
