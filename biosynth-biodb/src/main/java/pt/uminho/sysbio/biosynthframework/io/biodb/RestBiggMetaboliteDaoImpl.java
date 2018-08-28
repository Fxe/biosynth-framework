package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsService;
import pt.uminho.sysbio.biosynthframework.io.AbstractReadOnlyMetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.Bigg2ApiService.ListResult;
import retrofit2.Retrofit;

public class RestBiggMetaboliteDaoImpl extends AbstractReadOnlyMetaboliteDao<Bigg2MetaboliteEntity>
implements BiosDao<Bigg2MetaboliteEntity> {
  
  private static final Logger logger = LoggerFactory.getLogger(RestBiggMetaboliteDaoImpl.class);
  
  public static String DEFAULT_SERVICE_PATH = "http://bigg.ucsd.edu/api/v2/";
  
  public final Bigg2ApiService service;
  protected String databasePath = null;
  public boolean cacheData = true;
  public boolean cacheUse = true;
  public Map<String, Bigg2MetaboliteEntity> cache = new HashMap<> ();
  protected File localStorageFolder = null;
  
  /**
   * 
   * @param biggServicePath service url for the BiGG database API 
   * (default: http://bigg.ucsd.edu/api/v2)
   * @param version 
   */
  public RestBiggMetaboliteDaoImpl(String biggServicePath, String version, String localStorage) {
    super(version);
    String endPoint = biggServicePath;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .build();
    service = retrofit.create(Bigg2ApiService.class);
    
//    long connectionTimeout = 60;
//    long readTimeout = 60;
//    
//    final OkHttpClient okHttpClient = new OkHttpClient();
//    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
//    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
//    RestAdapter restAdapter = new RestAdapter.Builder()
//                  .setConverter(new Bigg2JsonConverter(databasePath))
//                  .setLogLevel(LogLevel.NONE)
//                  .setClient(new OkClient(okHttpClient))
//                  .setEndpoint(endPoint)
//                  .build();
//    service = restAdapter.create(Bigg2ApiService.class);
    this.databasePath = localStorage;
    if (databasePath != null && cacheData) {
      localStorageFolder = new File(databasePath + "/" + version);
      if (!localStorageFolder.exists()) {
        localStorageFolder.mkdirs();
        logger.info("mkdirs {}", localStorageFolder.getAbsolutePath());
      }
    }
  }
  
  /**
   * uses default endpoint
   * @param version
   */
  public RestBiggMetaboliteDaoImpl(String version, String localStorage) {
    this(DEFAULT_SERVICE_PATH, version, localStorage);
  }
  
  
  @Deprecated
  public RestBiggMetaboliteDaoImpl() {
    this(DEFAULT_SERVICE_PATH);
  }
  
  public static Bigg2MetaboliteEntity convert(Bigg2ApiMetabolite acpd) {
    if (acpd == null) {
      return null;
    }
    
    Bigg2MetaboliteEntity cpd = new Bigg2MetaboliteEntity();
    cpd.setEntry(acpd.bigg_id);
    cpd.setName(acpd.name);
    
    if (acpd.formulae != null) {
      cpd.setFormula(StringUtils.join(acpd.formulae, ';'));
    }
    
    if (acpd.compartments_in_models != null) {
//      System.out.println(acpd.compartments_in_models);
    }
    
    if (acpd.old_identifiers != null) {
      cpd.setOldIdentifiers(StringUtils.join(acpd.old_identifiers, ';'));
    }
    
    if (acpd.database_links != null) {
      for (Map<String, List<Map<String, Object>>> dblinks : acpd.database_links) {
        for (String db : dblinks.keySet()) {
          List<Map<String, Object>> data = dblinks.get(db);
          for (Map<String, Object> d : data) {
            String ref = (String) d.get("id");
            String link = (String) d.get("link");
            Bigg2MetaboliteCrossreferenceEntity xref = 
                new Bigg2MetaboliteCrossreferenceEntity(ReferenceType.DATABASE, db, ref);
            xref.setLink(link);
            cpd.getCrossreferences().add(xref);
          }
        }
      }
    }
    
    return cpd;
  }

  @Deprecated
  public RestBiggMetaboliteDaoImpl(String biggServicePath) {
    super("");
    String endPoint = biggServicePath;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .build();
    service = retrofit.create(Bigg2ApiService.class);
    
//    long connectionTimeout = 60;
//    long readTimeout = 60;
//    
//    final OkHttpClient okHttpClient = new OkHttpClient();
//    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
//    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
//    RestAdapter restAdapter = new RestAdapter.Builder()
//                  .setConverter(new Bigg2JsonConverter(databasePath))
//                  .setLogLevel(LogLevel.NONE)
//                  .setClient(new OkClient(okHttpClient))
//                  .setEndpoint(endPoint)
//                  .build();
//    service = restAdapter.create(Bigg2ApiService.class);
  }

  public static Bigg2ApiMetabolite getAndCacheCpd2(String id) {
    Bigg2ApiMetabolite cpd = null;
    File jsonFile = new File("/var/biodb/bigg2/metabolites/" + id + ".json");
    if (!jsonFile.exists()) {
      try (OutputStream os = new FileOutputStream(jsonFile)) {
        List<String> lines = IOUtils.readLines(
            new URL("http://bigg.ucsd.edu/api/v2/universal/metabolites/" + id).openStream(), Charset.defaultCharset());
        IOUtils.write(StringUtils.join(lines, '\n'), os, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      Bigg2JsonConverter converter = new Bigg2JsonConverter();
      try (InputStream is = new FileInputStream(jsonFile)) {
        cpd = converter.convert(Bigg2ApiMetabolite.class, is);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return cpd;
  }
  
  public Bigg2MetaboliteEntity getMetaboliteByEntryCache(String entry) {
    Bigg2MetaboliteEntity cpd = null;
    File cacheFile = new File(this.localStorageFolder.getAbsolutePath() + "/cpd/" + entry + ".json/");
    if (cacheFile.exists() && cacheFile.isFile()) {
      Bigg2JsonConverter converter = new Bigg2JsonConverter();
      try (InputStream is = new FileInputStream(cacheFile)) {
        Bigg2ApiMetabolite acpd = converter.convert(Bigg2ApiMetabolite.class, is);
        cpd = convert(acpd);
        cpd.setVersion(version);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return cpd;
  }
  
  public Bigg2MetaboliteEntity getMetaboliteByEntryRest(String entry) {
    List<String> data = null;
    Bigg2MetaboliteEntity cpd = null;
    try {
      URL url = new URL("http://bigg.ucsd.edu/api/v2/universal/metabolites/" + entry);
      data = IOUtils.readLines(url.openStream(), Charset.defaultCharset());
      Bigg2JsonConverter converter = new Bigg2JsonConverter();
      Bigg2ApiMetabolite acpd = converter.convert(Bigg2ApiMetabolite.class, new ByteArrayInputStream(StringUtils.join(data, '\n').getBytes()));
      cpd = convert(acpd);
      cpd.setVersion(version);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (cacheData) {
      cacheMetaboliteByEntryRest(entry + ".json", StringUtils.join(data, '\n'));
    }
    
    return cpd;
  }
  
  public File cacheMetaboliteByEntryRest(String filename, String data) {
    if (this.localStorageFolder.exists() && this.localStorageFolder.isDirectory()) {
      File folder = new File(this.localStorageFolder + "/cpd");
      if (!folder.exists()) {
        folder.mkdirs();
        logger.info("mkdirs {}", folder.getAbsolutePath());
      }
      
      File dataFile = new File(folder.getAbsolutePath() + "/" + filename);
      try (OutputStream os = new FileOutputStream(dataFile)) {
        IOUtils.write(data, os, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return dataFile;
    }
    
    return null;
  }
  
  @Override
  public Bigg2MetaboliteEntity getMetaboliteByEntry(String entry) {
//    Bigg2MetaboliteEntity cpd = null;
//    if (cache.containsKey(entry)) {
//      return cache.get(entry);
//    }
//    Bigg2ApiMetabolite acpd = service.getUniversalMetaboliteByEntry(entry);
//    Bigg2MetaboliteEntity cpd = convert(acpd);
//    
//    if (cpd != null) {
//      cache.put(entry, cpd);
//    }
    
    if (this.cacheUse) {
      Bigg2MetaboliteEntity cpd = getMetaboliteByEntryCache(entry);
      if (cpd != null) {
        return cpd;
      }
    }
    
    return getMetaboliteByEntryRest(entry);
  }

  
  public List<String> getAllMetaboliteEntriesRest() {
    List<String> result = new ArrayList<> ();
    ListResult query = service.listUniversalMetabolites();
    for (Map<String, Object> o : query.results) {
      String cpdEntry = (String) o.get("bigg_id");
      result.add(cpdEntry);
    }
    
    if (cacheData) {
      saveAllMetaboliteEntriesCache(result);
    }
    
    return result;
  }
  
  public List<String> getAllMetaboliteEntriesCache() {
    File queryCache = new File(this.localStorageFolder.getAbsolutePath() + "/query/cpd.txt");
    if (queryCache.exists() && queryCache.isFile()) {
      List<String> result = new ArrayList<> ();
      try (InputStream is = new FileInputStream(queryCache)) {
        result = IOUtils.readLines(is, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return result;
    }
    return null;
  }
  
  public File saveAllMetaboliteEntriesCache(List<String> string) {
    if (this.localStorageFolder.exists() && this.localStorageFolder.isDirectory()) {
      File queryFolder = new File(this.localStorageFolder + "/query");
      if (!queryFolder.exists()) {
        queryFolder.mkdirs();
        logger.info("mkdirs {}", queryFolder.getAbsolutePath());
      }
      
      File queryFile = new File(queryFolder.getAbsolutePath() + "/cpd.txt");
      try (OutputStream os = new FileOutputStream(queryFile)) {
        IOUtils.writeLines(string, "\n", os, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return queryFile;
    }
    
    return null;
  }
  
  @Override
  public List<String> getAllMetaboliteEntries() {
    List<String> result = new ArrayList<> ();
    
    if (this.cacheUse) {
      result = getAllMetaboliteEntriesCache();
      if (result != null) {
        return result;
      }
    }
    
    return getAllMetaboliteEntriesRest();
  }

//  @Override
//  public Serializable save(Bigg2MetaboliteEntity entity) {
//    throw new RuntimeException("Not Supported Operation");
//  }
  
  @Override
  public Bigg2MetaboliteEntity getMetaboliteById(Serializable id) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Bigg2MetaboliteEntity getByEntry(String entry) {
    return this.getMetaboliteByEntry(entry);
  }

  @Override
  public Bigg2MetaboliteEntity getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(Bigg2MetaboliteEntity o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(Bigg2MetaboliteEntity o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    return new HashSet<>(this.getAllMetaboliteEntries());
  }
}
