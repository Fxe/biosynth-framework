package pt.uminho.sysbio.biosynthframework.io.biodb.ncbi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezDatabase;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezRetmode;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsService;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class EutilsNcbiAssemblyDaoImpl implements BiosDao<EutilsAssemblyObject> {

  
  private static final Logger logger = LoggerFactory.getLogger(EutilsNcbiAssemblyDaoImpl.class);
  
  private EutilsService service;
  
  public EutilsNcbiAssemblyDaoImpl() {
    String endPoint = "http://eutils.ncbi.nlm.nih.gov/";
    long connectionTimeout = 60;
    long readTimeout = 60;
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(Level.NONE);
    final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                                                        .addInterceptor(interceptor)
                                                        .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                                                        .readTimeout(readTimeout, TimeUnit.SECONDS)
                                                        .build();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                              .addConverterFactory(SimpleXmlConverterFactory.create())
                                              .addConverterFactory(JacksonConverterFactory.create())
                                              .build();
    service = retrofit.create(EutilsService.class);
//    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
//    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
//    RestAdapter restAdapter = new RestAdapter.Builder()
//                  .setLogLevel(LogLevel.NONE)
//                  .setClient(new OkClient(okHttpClient))
//                  .setEndpoint(endPoint)
//                  .build();
//    service = restAdapter.create(EutilsService.class);
  }
  
  @Override
  public EutilsAssemblyObject getByEntry(String entry) {
    Map<String, Object> response = null;
    try {
      response = service.esearch(EntrezDatabase.assembly, entry, 10, 0, EntrezRetmode.json).execute().body();
      
      List<Object> idlist = JsonMapUtils.getList("idlist", JsonMapUtils.getMap("esearchresult", response));
      if (idlist.size() == 1) {
        String id = (String) idlist.iterator().next();
        Map<String, Object> summary = service.esummary(
            EntrezDatabase.assembly.toString(), id, EntrezRetmode.json).execute().body();
        Map<String, Object> data = JsonMapUtils.getMap(id, JsonMapUtils.getMap("result", summary));
        
        ObjectMapper om = new ObjectMapper();
        try {
          EutilsAssemblyObject assembly = om.readValue(om.writeValueAsString(data), EutilsAssemblyObject.class);
          if (assembly == null) {
            return null;
          }
          if (entry.equals(assembly.assemblyaccession) || assembly.synonym.values().contains(entry)) {
            return assembly;
          } else {
            logger.warn("result reject assemble accession does not match. {} -> {}", entry, assembly.assemblyaccession);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (idlist.size() > 1) {
        logger.warn("Results discarded. More than 1");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }

  @Override
  public EutilsAssemblyObject getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(EutilsAssemblyObject o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(EutilsAssemblyObject o) {
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
    // TODO Auto-generated method stub
    return null;
  }

}
