package pt.uminho.sysbio.biosynthframework.biodb.kegg.crawller;

import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenesDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenomeDaoImpl;

public class keggGenomeAndGenesRunnable implements Runnable, Callable<Integer>{

	private static final Logger LOGGER = LoggerFactory.getLogger(keggGenomeAndGenesRunnable.class);
	String genome;
	RestKeggGenesDaoImpl genes;
	RestKeggGenomeDaoImpl genomeDao;
	int id;
	
	public keggGenomeAndGenesRunnable(int id, String folder, String genome) {
		this.genome = genome;
		genes = new RestKeggGenesDaoImpl();
		genes.setLocalStorage(folder);
		genes.setSaveLocalStorage(true);
		genes.setUseLocalStorage(true);
		genes.createFolder();
		
		genomeDao = new RestKeggGenomeDaoImpl();
		genomeDao.setLocalStorage(folder);
		genomeDao.setSaveLocalStorage(true);
		genomeDao.setUseLocalStorage(true);
//		genomeDao.createFolder();
		this.id = id;
	}
	
	public void run() {
		LOGGER.info("Thread {} genome start", id );
		
		try {
			
			genomeDao.getByEntry(genome);
			Set<String> geneIds = genes.getAllGenesEntries(genome);
			for(String gId : geneIds){
				genes.getGeneByEntry(gId);
			}
			
			LOGGER.info("genome " + genome + "\tcompleted\t"+geneIds.size(), id );
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Thread {} genome stop", id );
		
	}

	@Override
	public Integer call() throws Exception {
		run();
		
		return 1;
	}
}
