package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class RestLipidmapsMetaboliteDaoImpl {
  
  public final LipidmapsApiService service;
  public String databasePath = null;;
  
  public RestLipidmapsMetaboliteDaoImpl() {
    String endPoint = "http://www.lipidmaps.org/rest";
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
    service = restAdapter.create(LipidmapsApiService.class);
  }
}
