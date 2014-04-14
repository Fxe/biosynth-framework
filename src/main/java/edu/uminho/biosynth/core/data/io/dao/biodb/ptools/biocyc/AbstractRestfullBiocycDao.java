package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.File;
import java.io.IOException;

import edu.uminho.biosynth.core.data.io.http.HttpRequest;
import edu.uminho.biosynth.util.BioSynthUtilsIO;

public abstract class AbstractRestfullBiocycDao extends AbstractBiocycDao {
	
	private String localStorage;
	private boolean useLocalStorage = false;
	private boolean saveLocalStorage = false;
	
	public String getLocalStorage() { return localStorage;}
	public void setLocalStorage(String localStorage) {
		this.localStorage = localStorage.trim().replaceAll("\\\\", "/");
		if ( !this.localStorage.endsWith("/")) this.localStorage = this.localStorage.concat("/");
	}
	
	protected String getLocalOrWeb(String restQuery, String localPath) throws IOException {
		String httpResponseString = null;
//		String dataFileStr = localStorage  + entityType + "/" + entry + "." + extension;
		File dataFile = new File(localPath);
		System.out.println(dataFile);
		boolean didFetch = false;
		//check local file
		if (useLocalStorage && dataFile.exists()) {
			//file not exist then fetch !
			httpResponseString = BioSynthUtilsIO.readFromFile(dataFile.getAbsolutePath());
		} else {
			//either not using local or datafile does not exists
			httpResponseString = HttpRequest.get(restQuery);
			didFetch = true;
		}
		
		
		if (saveLocalStorage && didFetch) {
//			System.out.println("SAVING !" + localPath);
			BioSynthUtilsIO.writeToFile(httpResponseString, localPath);			
		}
		
		return httpResponseString;
	}
	
	public boolean isUseLocalStorage() { return useLocalStorage;}
	public void setUseLocalStorage(boolean useLocalStorage) { this.useLocalStorage = useLocalStorage;}

	public boolean isSaveLocalStorage() { return saveLocalStorage;}
	public void setSaveLocalStorage(boolean saveLocalStorage) { this.saveLocalStorage = saveLocalStorage;}

}
