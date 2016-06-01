package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class EntrezTaxonomyService {
  private EutilsService eutilsService;

  public EntrezTaxonomyService() {
    
    String endPoint = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";
    long connectionTimeout = 60;
    long readTimeout = 60;
    
    // TODO Auto-generated constructor stub
    final OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
    RestAdapter restAdapter = new RestAdapter.Builder()
                  .setConverter(new EntrezTaxonomyConverter())
                  .setLogLevel(LogLevel.NONE)
                  .setClient(new OkClient(okHttpClient))
                  .setEndpoint(endPoint)
                  .build();
    eutilsService = restAdapter.create(EutilsService.class);
  }
  
  public EntrezTaxon getTaxonomy(long id) {
    EntrezTaxaSet taxaSet = eutilsService.efetchTaxonomy(Long.toString(id), 
                                                         EntrezRetmode.xml);
    
    if (taxaSet != null && taxaSet.taxons.size() > 0) {
      return taxaSet.taxons.get(0);
    }
    
    return null;
  }
  
  public List<EntrezTaxon> getTaxonomy(Set<Long> ids) {
    EntrezTaxaSet taxaSet = eutilsService.efetchTaxonomy(
        StringUtils.join(ids, ","), EntrezRetmode.xml);
    
    return taxaSet.taxons;
  }
}
