package pt.uminho.sysbio.biosynthframework.biodb.kegg.crawller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggECNumberDaoImpl;

public class KeggECNumbersRunnable implements Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(KeggECNumbersRunnable.class);
	Collection<String> ecNumbers;
	RestKeggECNumberDaoImpl rest;
	
	public KeggECNumbersRunnable(String folder, Collection<String> ecNumbers){
		this.ecNumbers = ecNumbers;
		rest = new RestKeggECNumberDaoImpl();
		rest.setLocalStorage(folder);
		rest.setSaveLocalStorage(true);
		rest.setUseLocalStorage(true);
		rest.createFolder();
	}
	
	public void run() {
		
		try {
			for(String ec : ecNumbers){
				rest.getECNumberByEntry(ec);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}
}
