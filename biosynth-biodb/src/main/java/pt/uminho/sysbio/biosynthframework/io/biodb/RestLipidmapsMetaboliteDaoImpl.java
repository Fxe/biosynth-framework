package pt.uminho.sysbio.biosynthframework.io.biodb;

import okhttp3.OkHttpClient;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezTaxonomyConverter;
import retrofit2.Retrofit;

public class RestLipidmapsMetaboliteDaoImpl {
  
  public final LipidmapsApiService service;
  public String databasePath = null;;
  
  public RestLipidmapsMetaboliteDaoImpl() {
    String endPoint = "http://www.lipidmaps.org/rest";
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(endPoint)
                                              .build();
    service = retrofit.create(LipidmapsApiService.class);
//    
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
//    service = restAdapter.create(LipidmapsApiService.class);
  }
}
