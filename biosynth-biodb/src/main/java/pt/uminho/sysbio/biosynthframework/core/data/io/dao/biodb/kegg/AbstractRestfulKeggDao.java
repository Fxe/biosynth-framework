package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;

public abstract class AbstractRestfulKeggDao<T extends AbstractBiosynthEntity> implements BiosDao<T>{

  private static final Logger logger = LoggerFactory.getLogger(AbstractRestfulKeggDao.class);
  
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
  
  public static String getString(Map<String, Object> odata, String key) {
    if (!DataUtils.empty(odata.get(key))) {
      return odata.get(key).toString().trim();
    }
    
    return null;
  }
  
  public String getPath(String...path) {
    List<String> p = new ArrayList<> ();
    p.add(localStorage);
    p.add(databaseVersion);
    for (String s : path) {
      p.add(s);
    }
    return StringUtils.join(p, '/');
  }

  protected String getLocalOrWeb(String restQuery, String localPath) throws IOException {
    String httpResponseString = null;
    //		String dataFileStr = localStorage  + entityType + "/" + entry + "." + extension;
    File dataFile = new File(localPath);
    
    boolean didFetch = false;
    //check local file
    if (useLocalStorage && dataFile.exists()) {
      logger.debug("[USE_LOCAL] found local data {}", dataFile.getAbsolutePath());
      //file not exist then fetch !
      httpResponseString = IOUtils.readFromFile(dataFile.getAbsolutePath());
    } else {
      //either not using local or datafile does not exists
      
      httpResponseString = IOUtils.getUrlAsString(restQuery); //HttpRequest.get(restQuery);
      didFetch = true;
    }

    if (httpResponseString == null || httpResponseString.isEmpty()) {
      return null;
    }

    if (saveLocalStorage && didFetch) {
      logger.info("saving {}", localPath);
      IOUtils.writeToFile(httpResponseString, localPath, true);
    }

    return httpResponseString;
  }

  public boolean isUseLocalStorage() { return useLocalStorage;}
  public void setUseLocalStorage(boolean useLocalStorage) { this.useLocalStorage = useLocalStorage;}

  public boolean isSaveLocalStorage() { return saveLocalStorage;}
  public void setSaveLocalStorage(boolean saveLocalStorage) { this.saveLocalStorage = saveLocalStorage;}

  @Override
  public T getById(long id) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public Long save(T o) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public boolean delete(T o) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public Set<Long> getAllIds() {
    throw new RuntimeException("Unsupported Operation");
  }
}
