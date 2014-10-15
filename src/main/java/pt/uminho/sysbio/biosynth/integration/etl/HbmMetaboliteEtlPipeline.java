package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Metabolite;


public class HbmMetaboliteEtlPipeline<SRC extends Metabolite, DST extends Metabolite>
implements EtlPipeline<SRC, DST> {

	private static Logger LOGGER = LoggerFactory.getLogger(HbmMetaboliteEtlPipeline.class);
	
	private SessionFactory sessionFactory;
	
	private EtlExtract<SRC> extractSubsystem;
	private EtlTransform<SRC, DST> etlTransform;
	private EtlLoad<DST> etlLoad;
	
	private int batchSize = 100;
	
	private boolean skipLoad = false;
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public void setExtractSubsystem(EtlExtract<SRC> extractSubsystem) { 
		this.extractSubsystem = extractSubsystem;
	}; 
	
	@Override
	public void setEtlDataCleasingSubsystem(
			EtlDataCleansing<DST> dataCleasingSubsystem) {
		// TODO Auto-generated method stub
		
	}
	
	public EtlTransform<SRC, DST> getEtlTransform() { return etlTransform;}
	@Override
	public void setTransformSubsystem(EtlTransform<SRC, DST> etlTransform) { this.etlTransform = etlTransform;}

	public EtlLoad<DST> getEtlLoad() { return etlLoad;}
	@Override
	public void setLoadSubsystem(EtlLoad<DST> etlLoad) { this.etlLoad = etlLoad;}

	public int getBatchSize() { return batchSize;}
	public void setBatchSize(int batchSize) { this.batchSize = batchSize;}
	
	public boolean isSkipLoad() { return skipLoad;}
	public void setSkipLoad(boolean skipLoad) { this.skipLoad = skipLoad;}

	@Override
	public void etl(Serializable id) {
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		//SRC = ETL EXTRACT(Entry)
		SRC src = extractSubsystem.extract(id);
		
		//DST = ETL TRANSFORM(SRC)
		DST dst = etlTransform.etlTransform(src);

		//ETL LOAD(DST)
		if (!skipLoad) etlLoad.etlLoad(dst);
		
		tx.commit();
	}
	
	@Override
	public void etl() {
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		int i = 0;
		for (Serializable entry : extractSubsystem.getAllKeys()) {
			//SRC = ETL EXTRACT(Entry)
			SRC src = extractSubsystem.extract(entry);
			
			//DST = ETL TRANSFORM(SRC)
			DST dst = etlTransform.etlTransform(src);

			//ETL LOAD(DST)
			if (!skipLoad) etlLoad.etlLoad(dst);
			
			i++;
			if ((i % batchSize) == 0) {
				LOGGER.debug(String.format("Commit ! %d", i));
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
	}


}
