package pt.uminho.sysbio.biosynthframework.biodb.kegg.crawller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;

@Deprecated
public class KeggKORunnable implements Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(KeggKORunnable.class);
	Collection<String> entities;
	RestKeggKOsDaoImpl rest;
	
	public KeggKORunnable(String folder, Collection<String> entities){
		this.entities = entities;
		rest = new RestKeggKOsDaoImpl();
		rest.setLocalStorage(folder);
		rest.setSaveLocalStorage(true);
		rest.setUseLocalStorage(true);
		rest.createFolder();
	}
	
	
	public void run() {
		try {
			for(String entity : entities){
				rest.getKOByEntry(entity);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}
}
