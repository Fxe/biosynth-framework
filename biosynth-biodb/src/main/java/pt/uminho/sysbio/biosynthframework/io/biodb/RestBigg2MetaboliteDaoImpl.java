package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.squareup.okhttp.OkHttpClient;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.Bigg2ApiService.ListResult;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class RestBigg2MetaboliteDaoImpl implements MetaboliteDao<Bigg2MetaboliteEntity>{
  
  public static String DEFAULT_SERVICE_PATH = "http://bigg.ucsd.edu/api/v2";
  
  public final Bigg2ApiService service;
  public String databasePath = null;
  public Map<String, Bigg2MetaboliteEntity> cache = new HashMap<> ();
  
  public RestBigg2MetaboliteDaoImpl() {
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
  
  /**
   * 
   * @param biggServicePath service url for the BiGG database API 
   * (default: http://bigg.ucsd.edu/api/v2)
   */
  public RestBigg2MetaboliteDaoImpl(String biggServicePath) {
    String endPoint = biggServicePath;
    long connectionTimeout = 60;
    long readTimeout = 60;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
    RestAdapter restAdapter = new RestAdapter.Builder()
                  .setConverter(new Bigg2JsonConverter(databasePath))
                  .setLogLevel(LogLevel.NONE)
                  .setClient(new OkClient(okHttpClient))
                  .setEndpoint(endPoint)
                  .build();
    service = restAdapter.create(Bigg2ApiService.class);
  }

  @Override
  public Bigg2MetaboliteEntity getMetaboliteByEntry(String entry) {
    if (cache.containsKey(entry)) {
      return cache.get(entry);
    }
    Bigg2ApiMetabolite acpd = service.getUniversalMetaboliteByEntry(entry);
    Bigg2MetaboliteEntity cpd = convert(acpd);
    
    if (cpd != null) {
      cache.put(entry, cpd);
    }
    
    return cpd;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    ListResult query = service.listUniversalMetabolites();
    List<String> result = new ArrayList<> ();
    for (Map<String, Object> o : query.results) {
      String cpdEntry = (String) o.get("bigg_id");
      result.add(cpdEntry);
    }
    return result;
  }

  @Override
  public Serializable save(Bigg2MetaboliteEntity entity) {
    throw new RuntimeException("Not Supported Operation");
  }
  
  @Override
  public Bigg2MetaboliteEntity getMetaboliteById(Serializable id) {
    throw new RuntimeException("Not Supported Operation");
  }
  
  @Override
  public Bigg2MetaboliteEntity saveMetabolite(Bigg2MetaboliteEntity metabolite) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Not Supported Operation");
  }
}
