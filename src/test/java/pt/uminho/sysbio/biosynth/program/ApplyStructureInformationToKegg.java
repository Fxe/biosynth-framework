package pt.uminho.sysbio.biosynth.program;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import pt.uminho.sysbio.biosynth.chemanalysis.openbabel.OpenBabelWrapper;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggCompoundMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.HbmKeggDrugMetaboliteDaoImpl;

public class ApplyStructureInformationToKegg {

	
	private static void doForDrugs(SessionFactory sessionFactory) {
		HbmKeggDrugMetaboliteDaoImpl dao = new HbmKeggDrugMetaboliteDaoImpl();
		dao.setSessionFactory(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		int i = 0;
		for (String entry : dao.getAllMetaboliteEntries()) {
			KeggDrugMetaboliteEntity cpd = dao.getMetaboliteByEntry(entry);
			String mol2d = cpd.getMol2d();
			
			if (mol2d != null) {
				System.out.println(cpd.getEntry());
				cpd.setInchi(OpenBabelWrapper.convert(mol2d, "mol", "inchi"));
				cpd.setSmiles(OpenBabelWrapper.convertMol2dToSmiles(mol2d));
				cpd.setInchiKey(OpenBabelWrapper.convert(mol2d, "mol", "inchikey"));
				dao.save(cpd);
				i++;
			}
			if (i % 10 == 0) {
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
	}
	
	private static void doForCompounds(SessionFactory sessionFactory) {
		HbmKeggCompoundMetaboliteDaoImpl dao = new HbmKeggCompoundMetaboliteDaoImpl();
		dao.setSessionFactory(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		int i = 0;
		for (String entry : dao.getAllMetaboliteEntries()) {
			KeggCompoundMetaboliteEntity cpd = dao.getMetaboliteByEntry(entry);
			String mol2d = cpd.getMol2d();
			
			if (mol2d != null) {
				System.out.println(cpd.getEntry());
				cpd.setInchi(OpenBabelWrapper.convert(mol2d, "mol", "inchi"));
				cpd.setSmiles(OpenBabelWrapper.convertMol2dToSmiles(mol2d));
				cpd.setInchiKey(OpenBabelWrapper.convert(mol2d, "mol", "inchikey"));
				dao.save(cpd);
				i++;
			}
			if (i % 10 == 0) {
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
	}
	
	public static void main(String args[]) {
		OpenBabelWrapper.initializeLibrary();
		SessionFactory sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		
		sessionFactory.openSession();
		
		doForDrugs(sessionFactory);
		doForCompounds(sessionFactory);

		

		
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
	}
}
