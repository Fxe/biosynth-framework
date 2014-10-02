package pt.uminho.sysbio.biosynth.integration.etl;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.components.Metabolite;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;


public class HbmMetaboliteEtlPipeline<SRC extends Metabolite, DST extends Metabolite> {

	private static Logger LOGGER = LoggerFactory.getLogger(HbmMetaboliteEtlPipeline.class);
	
	private SessionFactory sessionFactory;
	private MetaboliteDao<SRC> daoSrc;
	private MetaboliteDao<DST> daoDst;
	
	private EtlTransform<SRC, DST> etlTransform;
	private EtlLoad<DST> etlLoad;
	
	private int batchSize = 100;
	
	public void etl() {
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		int i = 0;
		for (String entry : daoDst.getAllMetaboliteEntries()) {
			//SRC = ETL EXTRACT(Entry)
			//DST = ETL TRANSFORM(SRC)
			//ETL LOAD(DST)
			SRC src = daoSrc.getMetaboliteByEntry(entry);
			DST dst = etlTransform.etlTransform(src);
			etlLoad.etlLoad(dst);
			
			i++;
			if ((i % batchSize) == 0) {
				LOGGER.debug("Commit ! %d", i);
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
	}
}
