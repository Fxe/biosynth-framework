package pt.uminho.sysbio.biosynthframework.biodb.kegg.crawller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.io.biodb.kegg.RestKeggModuleDaoImpl;

@Deprecated
public class keggModuleRunnable implements Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(keggModuleRunnable.class);
	Collection<String> entities;
	RestKeggModuleDaoImpl rest;
	
	public keggModuleRunnable(String folder, Collection<String> entities){
		this.entities = entities;
		rest = new RestKeggModuleDaoImpl();
		rest.setLocalStorage(folder);
		rest.setSaveLocalStorage(true);
		rest.setUseLocalStorage(true);
//		rest.createFolder();
	}
	
	
	public void run() {
		try {
			for(String entity : entities){
				rest.getByEntry(entity);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}
}
