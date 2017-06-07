package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class DefaultUniprotService {
  public final UniprotService service;
  
  public DefaultUniprotService() {
    String endPoint = "http://www.uniprot.org";
    long connectionTimeout = 60;
    long readTimeout = 60;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
    RestAdapter restAdapter = new RestAdapter.Builder()
                  .setConverter(new UniprotEntryConverter())
                  .setLogLevel(LogLevel.NONE)
                  .setClient(new OkClient(okHttpClient))
                  .setEndpoint(endPoint)
                  .build();
    service = restAdapter.create(UniprotService.class);
  }
  
  public UniprotEntry getEntry(String entry) {
    UniprotResult result = service.getByEntry(entry);
    if (result != null && result.entries.size() == 1) {
      return result.entries.iterator().next();
    }
    
    return null;
  }
  
  //proteome:UP000001425
  public UniprotResult getGetEntriesByProteome(String uprotmEntry) {
    String query = String.format("proteome:%s", uprotmEntry);
    UniprotResult result = service.query(query, "xml");
    return result;
  }
  
  
  public UniprotResult getGetEntriesByTaxonomy(long txid) {
    String query = String.format("organism:%d", txid);
    UniprotResult result = service.query(query, "xml");
    return result;
  }
  
  public Object getEntry_(String entry) {
    
    return service.getByEntry_(entry);
  }
}
