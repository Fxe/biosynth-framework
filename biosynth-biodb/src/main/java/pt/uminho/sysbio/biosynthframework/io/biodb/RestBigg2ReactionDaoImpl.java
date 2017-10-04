package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiReaction;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.Bigg2ApiService.ListResult;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class RestBigg2ReactionDaoImpl implements ReactionDao<Bigg2ReactionEntity> {

  public static String DEFAULT_SERVICE_PATH = "http://bigg.ucsd.edu/api/v2";
  
  public final Bigg2ApiService service;
  public String databasePath = null;
  public Map<String, Bigg2ReactionEntity> cache = new HashMap<> ();
  
  public RestBigg2ReactionDaoImpl() {
    this(DEFAULT_SERVICE_PATH);
  }
  
  public RestBigg2ReactionDaoImpl(String biggServicePath) {
    String endPoint = biggServicePath;
    long connectionTimeout = 60;
    long readTimeout = 60;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
//    okHttpClient.networkInterceptors().add(REwr)
    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
    RestAdapter restAdapter = new RestAdapter.Builder()
//        .setp
//        .setRequestInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                  .setConverter(new Bigg2JsonConverter(databasePath))
                  .setLogLevel(LogLevel.NONE)
                  .setClient(new OkClient(okHttpClient))
                  .setEndpoint(endPoint)
                  .build();
    service = restAdapter.create(Bigg2ApiService.class);
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

  @Override
  public Bigg2ReactionEntity getReactionByEntry(String entry) {
    if (cache.containsKey(entry)) {
      return cache.get(entry);
    }
    Bigg2ApiReaction arxn = service.getUniversalReactionByEntry(entry);
    Bigg2ReactionEntity rxn = convert(arxn);
    
    if (rxn != null) {
      cache.put(entry, rxn);
    }
    
    return rxn;
  }
  
  @Override
  public Set<String> getAllReactionEntries() {
    ListResult query = service.listUniversalReactions();
    Set<String> result = new HashSet<> ();
    for (Map<String, Object> o : query.results) {
      String cpdEntry = (String) o.get("bigg_id");
      result.add(cpdEntry);
    }
    return result;
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
}
