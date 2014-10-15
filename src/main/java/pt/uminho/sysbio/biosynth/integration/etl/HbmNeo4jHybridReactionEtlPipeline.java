package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Reaction;

public class HbmNeo4jHybridReactionEtlPipeline<SRC extends Reaction, DST extends Reaction>
implements EtlPipeline<SRC, DST> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HbmNeo4jHybridReactionEtlPipeline.class);
	
	private SessionFactory sessionFactory;
	private GraphDatabaseService graphDatabaseService;
	
	private EtlDataCleansing<DST> dataCleasingSubsystem;
	private EtlExtract<SRC> extractSubsystem;
	private EtlTransform<SRC, DST> transformSubsystem;
	private EtlLoad<DST> loadSubsystem;
	
	private int batchSize = 100;
	private boolean skipLoad = false;
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

	public GraphDatabaseService getGraphDatabaseService() { return graphDatabaseService;}
	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) { this.graphDatabaseService = graphDatabaseService;}
	
	public int getBatchSize() { return batchSize;}
	public void setBatchSize(int batchSize) { this.batchSize = batchSize;}
	
	public boolean isSkipLoad() { return skipLoad;}
	public void setSkipLoad(boolean skipLoad) { this.skipLoad = skipLoad;}
	
	@Override
	public void setEtlDataCleasingSubsystem(
			EtlDataCleansing<DST> dataCleasingSubsystem) {
		this.dataCleasingSubsystem = dataCleasingSubsystem;
	}
	
	@Override
	public void setExtractSubsystem(EtlExtract<SRC> extractSubsystem) {
		this.extractSubsystem = extractSubsystem;
	}

	@Override
	public void setTransformSubsystem(EtlTransform<SRC, DST> transformSubsystem) {
		this.transformSubsystem = transformSubsystem;
	}

	@Override
	public void setLoadSubsystem(EtlLoad<DST> loadSubsystem) {
		this.loadSubsystem = loadSubsystem;
	}
	
	@Override
	public void etl(Serializable id) {
		if (this.dataCleasingSubsystem == null) {
			LOGGER.warn("No data cleasing system attached");
		}
		
		org.hibernate.Transaction hbmTx = sessionFactory.getCurrentSession().beginTransaction();
		org.neo4j.graphdb.Transaction neoTx = graphDatabaseService.beginTx();
		

		//SRC = ETL EXTRACT(Entry)
		SRC src = extractSubsystem.extract(id);
		if (src == null) {
			LOGGER.warn(String.format("Unable to extract entity %s", id));
			return;
		}
		
		//DST = ETL TRANSFORM(SRC)
		DST dst = transformSubsystem.etlTransform(src);

		if (this.dataCleasingSubsystem != null)
			this.dataCleasingSubsystem.etlCleanse(dst);
		
		//ETL LOAD(DST)
		System.out.println(dst);
		if (!skipLoad) loadSubsystem.etlLoad(dst);

		
		hbmTx.rollback();
		neoTx.success();
		neoTx.close();
	}

	@Override
	public void etl() {
		
		if (this.dataCleasingSubsystem == null) {
			LOGGER.warn("No data cleasing system attached");
		}
		
		org.hibernate.Transaction hbmTx = sessionFactory.getCurrentSession().beginTransaction();
		org.neo4j.graphdb.Transaction neoTx = graphDatabaseService.beginTx();
		
		int i = 0;
		for (Serializable entry : extractSubsystem.getAllKeys()) {
			//SRC = ETL EXTRACT(Entry)
			SRC src = extractSubsystem.extract(entry);
			
			//DST = ETL TRANSFORM(SRC)
			DST dst = transformSubsystem.etlTransform(src);

			if (this.dataCleasingSubsystem != null)
				this.dataCleasingSubsystem.etlCleanse(dst);
			
			//ETL LOAD(DST)
			if (!skipLoad) loadSubsystem.etlLoad(dst);
			
			i++;
			if ((i % batchSize) == 0) {
				LOGGER.debug(String.format("Commit ! %d", i));
				neoTx.success();
				neoTx.close();
				neoTx = graphDatabaseService.beginTx();
				
				hbmTx.rollback();
				hbmTx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		hbmTx.rollback();
		neoTx.success();
	}

	
	
}
