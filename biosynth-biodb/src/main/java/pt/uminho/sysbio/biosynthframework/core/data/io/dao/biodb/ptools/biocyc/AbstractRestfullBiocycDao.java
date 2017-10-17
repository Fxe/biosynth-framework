package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;

public abstract class AbstractRestfullBiocycDao extends AbstractBiocycDao {

  private final static Logger logger = LoggerFactory.getLogger(AbstractRestfullBiocycDao.class); 

  protected String localStorage;
  protected boolean useLocalStorage = false;
  protected boolean saveLocalStorage = false;
  protected String databaseVersion = "latest";
  
  public String getDatabaseVersion() { return databaseVersion;}
  public void setDatabaseVersion(String databaseVersion) { this.databaseVersion = databaseVersion;}
  
  public String getLocalStorage() { return localStorage;}
  public void setLocalStorage(String localStorage) {
    this.localStorage = localStorage.trim().replaceAll("\\\\", "/");
    if ( !this.localStorage.endsWith("/")) this.localStorage = this.localStorage.concat("/");
  }
  
  public String getPath(String...parts) {
    List<String> p = new ArrayList<> ();
    p.add(localStorage);
    p.add(databaseVersion);
    p.add(pgdb);
    for (String s : parts) {
      p.add(s);
    }
    return StringUtils.join(p, "/");
  }

  protected String getLocalOrWeb(String restQuery, String localPath) throws IOException {
    String httpResponseString = null;
    //		String dataFileStr = localStorage  + entityType + "/" + entry + "." + extension;
    File dataFile = new File(localPath);

    logger.debug("File: " + dataFile);

    boolean didFetch = false;
    //check local file
    if (useLocalStorage && dataFile.exists()) {
      //file not exist then fetch !
      httpResponseString = IOUtils.readFromFile(dataFile.getAbsolutePath());
    } else {
      //either not using local or datafile does not exists
      httpResponseString = HttpRequest.get(restQuery);
      didFetch = true;
    }


    if (saveLocalStorage && didFetch) {
      logger.debug("Write: " + localPath);
      IOUtils.writeToFile(httpResponseString, localPath, true);
//      IOUtils.writeToFile(httpResponseString, localPath);			
    }

    return httpResponseString;
  }

  public boolean isUseLocalStorage() { return useLocalStorage;}
  public void setUseLocalStorage(boolean useLocalStorage) { this.useLocalStorage = useLocalStorage;}

  public boolean isSaveLocalStorage() { return saveLocalStorage;}
  public void setSaveLocalStorage(boolean saveLocalStorage) { this.saveLocalStorage = saveLocalStorage;}

}
