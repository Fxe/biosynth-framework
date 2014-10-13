package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;

import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

public abstract class AbstractRestfulKeggDao {
	
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
		
		if (httpResponseString.isEmpty()) return null;
		
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
