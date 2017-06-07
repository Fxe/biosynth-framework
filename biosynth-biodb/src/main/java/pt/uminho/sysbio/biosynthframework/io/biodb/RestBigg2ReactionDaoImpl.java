package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiReaction;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class RestBigg2ReactionDaoImpl implements ReactionDao<Bigg2ReactionEntity> {

  public final Bigg2ApiService service;
  
  public String databasePath = null;;
  
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
  
  public RestBigg2ReactionDaoImpl() {
    String endPoint = "http://bigg.ucsd.edu/api/v2";
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
  
  @Override
  public Bigg2ReactionEntity getReactionById(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Bigg2ReactionEntity getReactionByEntry(String entry) {
    Bigg2ApiReaction arxn = service.getUniversalReactionByEntry(entry);
    
    return null;
  }

  @Override
  public Bigg2ReactionEntity saveReaction(Bigg2ReactionEntity reaction) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<Long> getAllReactionIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllReactionEntries() {
    // TODO Auto-generated method stub
    return null;
  }
}
