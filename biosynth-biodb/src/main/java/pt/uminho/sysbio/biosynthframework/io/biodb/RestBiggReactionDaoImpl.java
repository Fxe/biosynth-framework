package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
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
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiReaction;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezTaxonomyConverter;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsService;
import pt.uminho.sysbio.biosynthframework.io.AbstractReadOnlyReactionDao;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.Bigg2ApiService.ListResult;
import retrofit2.Retrofit;

public class RestBiggReactionDaoImpl extends AbstractReadOnlyReactionDao<Bigg2ReactionEntity> 
implements BiosDao<Bigg2ReactionEntity> {

  private static final Logger logger = LoggerFactory.getLogger(RestBiggReactionDaoImpl.class);
  
  public static String DEFAULT_SERVICE_PATH = "http://bigg.ucsd.edu/api/v2/";
  
  public boolean cacheData = true;
  public boolean cacheUse = true;
  protected File localStorageFolder = null;
  public final Bigg2ApiService service;
  public String databasePath = null;
  public Map<String, Bigg2ReactionEntity> cache = new HashMap<> ();
  
  public RestBiggReactionDaoImpl(String biggServicePath, String version, String localStorage) {
    super(version);
    String endPoint = biggServicePath;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .addConverterFactory(new EntrezTaxonomyConverter(null))
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
  
  public RestBiggReactionDaoImpl(String version, String localStorage) {
    this(DEFAULT_SERVICE_PATH, version, localStorage);
  }
  
  @Deprecated
  public RestBiggReactionDaoImpl() {
    this(DEFAULT_SERVICE_PATH);
  }
  
  @Deprecated
  public RestBiggReactionDaoImpl(String biggServicePath) {
    super("");
    String endPoint = biggServicePath;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .addConverterFactory(new Bigg2JsonConverter(null))
                                              .build();
    service = retrofit.create(Bigg2ApiService.class);
    
//    long connectionTimeout = 60;
//    long readTimeout = 60;
//    
//    final OkHttpClient okHttpClient = new OkHttpClient();
////    okHttpClient.networkInterceptors().add(REwr)
//    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
//    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
//    RestAdapter restAdapter = new RestAdapter.Builder()
////        .setp
////        .setRequestInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//                  .setConverter(new Bigg2JsonConverter(databasePath))
//                  .setLogLevel(LogLevel.NONE)
//                  .setClient(new OkClient(okHttpClient))
//                  .setEndpoint(endPoint)
//                  .build();
//    service = restAdapter.create(Bigg2ApiService.class);
  }
  
//  private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//      Response originalResponse = chain.proceed(chain.request());
//      if (Utils.isNetworkAvailable(context)) {
//          int maxAge = 60; // read from cache for 1 minute
//          return originalResponse.newBuilder()
//                  .header("Cache-Control", "public, max-age=" + maxAge)
//                  .build();
//      } else {
//          int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
//          return originalResponse.newBuilder()
//                  .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                  .build();
//      }
//  }
//    
//    
//  };
  
  public static Bigg2ReactionEntity convert(Bigg2ApiReaction arxn) {
    if (arxn == null) {
      return null;
    }
    

    
    List<Bigg2ReactionMetaboliteEntity> metabolites = new ArrayList<> ();
    for (Map<String, Object> scomp : arxn.metabolites) {
      String cpdEntry = scomp.get("bigg_id").toString();
      String compartmentBiggId = scomp.get("compartment_bigg_id").toString();
      String cpdName = scomp.get("name").toString();
      double stoichiometry = 
          Double.parseDouble(scomp.get("stoichiometry").toString());
      Bigg2ReactionMetaboliteEntity metabolite = new Bigg2ReactionMetaboliteEntity();
      metabolite.setStoichiometry(stoichiometry);
      metabolite.setCpdEntry(cpdEntry);
      metabolite.setName(cpdName);
      metabolite.setCompartmentBiggId(compartmentBiggId);
      
      metabolites.add(metabolite);
    }
    
    
    List<Bigg2ReactionCrossreferenceEntity> crossreferences = new ArrayList<> ();
    for (Map<String, List<Map<String, Object>>> dblinks : arxn.database_links) {
      for (String db : dblinks.keySet()) {
        ReferenceType rtype = ReferenceType.DATABASE;
        if (db.equals("EC Number")) {
          rtype = ReferenceType.ECNUMBER;
        }
        List<Map<String, Object>> data = dblinks.get(db);
        for (Map<String, Object> d : data) {
          String ref = (String) d.get("id");
          String link = (String) d.get("link");
          Bigg2ReactionCrossreferenceEntity xref = 
              new Bigg2ReactionCrossreferenceEntity(rtype, db, ref);
          xref.setLink(link);
          crossreferences.add(xref);
        }
      }
    }
    
    List<String> models = new ArrayList<> ();
    for (Map<String, String> record : arxn.models_containing_reaction) {
      String entry = record.get("bigg_id").toString();
      models.add(entry);
    }
    
    
    Bigg2ReactionEntity rxn = new Bigg2ReactionEntity();
    rxn.setEntry(arxn.bigg_id);
    rxn.setName(arxn.name);
    rxn.setReactionString(arxn.reaction_string != null ? arxn.reaction_string.trim() : null);
    rxn.setPseudoreaction(arxn.pseudoreaction);
    rxn.setMetabolites(metabolites);
    rxn.setModels(models);
    rxn.setCrossreferences(crossreferences);
    
    return rxn;
  }

  public Bigg2ReactionEntity getByEntryCache(String entry) {
    Bigg2ReactionEntity rxn = null;
    File cacheFile = new File(this.localStorageFolder.getAbsolutePath() + "/rxn/" + entry + ".json/");
    if (cacheFile.exists() && cacheFile.isFile()) {
      Bigg2JsonConverter converter = new Bigg2JsonConverter();
      try (InputStream is = new FileInputStream(cacheFile)) {
        Bigg2ApiReaction arxn = converter.convert(Bigg2ApiReaction.class, is);
        rxn = convert(arxn);
        rxn.setVersion(version);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return rxn;
  }
  
  public File cacheData(String filename, String data) {
    if (this.localStorageFolder == null) {
      return null;
    }
    if (this.localStorageFolder.exists() && this.localStorageFolder.isDirectory()) {
      File folder = new File(this.localStorageFolder + "/rxn");
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
  
  public Bigg2ReactionEntity getByEntryRest(String entry) {
    List<String> data = null;
    Bigg2ReactionEntity rxn = null;
    try {
      URL url = new URL(DEFAULT_SERVICE_PATH + "/universal/reactions/" + entry);
      data = IOUtils.readLines(url.openStream(), Charset.defaultCharset());
      Bigg2JsonConverter converter = new Bigg2JsonConverter();
      Bigg2ApiReaction arxn = converter.convert(Bigg2ApiReaction.class, new ByteArrayInputStream(StringUtils.join(data, '\n').getBytes()));
      rxn = convert(arxn);
      rxn.setVersion(version);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (cacheData) {
      cacheData(entry + ".json", StringUtils.join(data, '\n'));
    }
    
    return rxn;
  }
  
  @Override
  public Bigg2ReactionEntity getReactionByEntry(String entry) {
    if (this.cacheUse) {
      Bigg2ReactionEntity rxn = getByEntryCache(entry);
      if (rxn != null) {
        return rxn;
      }
    }
    
    return getByEntryRest(entry);
  }
  
  public Set<String> getAllMetaboliteEntriesRest() {
    Set<String> result = new HashSet<> ();
    ListResult query = service.listUniversalReactions();
    
    for (Map<String, Object> o : query.results) {
      String cpdEntry = (String) o.get("bigg_id");
      result.add(cpdEntry);
    }
    
    if (cacheData) {
      saveAllReactionEntriesCache(result);
    }
    
    return result;
  }
  
  public Collection<String> getAllReactionEntriesCache() {
    if (this.localStorageFolder == null) {
      return null;
    }
    File queryCache = new File(this.localStorageFolder.getAbsolutePath() + "/query/rxn.txt");
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
  
  public File saveAllReactionEntriesCache(Collection<String> string) {
    if (this.localStorageFolder == null) {
      return null;
    }
    if (this.localStorageFolder.exists() && this.localStorageFolder.isDirectory()) {
      File queryFolder = new File(this.localStorageFolder + "/query");
      if (!queryFolder.exists()) {
        queryFolder.mkdirs();
        logger.info("mkdirs {}", queryFolder.getAbsolutePath());
      }
      
      File queryFile = new File(queryFolder.getAbsolutePath() + "/rxn.txt");
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
  public Set<String> getAllReactionEntries() {
    Set<String> result = null;
    
    if (this.cacheUse) {
      Collection<String> a = getAllReactionEntriesCache();
      if (a != null) {
        result = new HashSet<>(a);
      }
      if (result != null) {
        return result;
      }
    }
    
    return getAllMetaboliteEntriesRest();
  }
  
  @Override
  public Bigg2ReactionEntity getReactionById(Long id) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Bigg2ReactionEntity saveReaction(Bigg2ReactionEntity reaction) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Set<Long> getAllReactionIds() {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Bigg2ReactionEntity getByEntry(String entry) {
    return this.getReactionByEntry(entry);
  }

  @Override
  public Bigg2ReactionEntity getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(Bigg2ReactionEntity o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(Bigg2ReactionEntity o) {
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
    return this.getAllReactionEntries();
  }
}
