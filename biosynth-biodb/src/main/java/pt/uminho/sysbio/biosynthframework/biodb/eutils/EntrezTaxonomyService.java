package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class EntrezTaxonomyService {
  public EutilsService eutilsService;

  public EntrezTaxonomyService() {
    
    String endPoint = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";
    long connectionTimeout = 60;
    long readTimeout = 60;
    
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
  
  public Object fetch(EntrezDatabase db, String id) {
    return eutilsService.efetch(db, id, EntrezRetmode.xml);
  }
  public Object fetch(EntrezDatabase db, long id) {
    return eutilsService.efetch(db, Long.toString(id), EntrezRetmode.xml);
  }
  
  public EntrezProtein getProtein(String id) {
    EntrezProtein proteinSet = eutilsService.efetchProtein(id, EntrezRetmode.xml);
    System.out.println(proteinSet);
    return null;
  }
  
  public EntrezSearchResult searchGenes(long txId, int max, int start) {
    EntrezSearchResult taxaSet = eutilsService.esearch(
        EntrezDatabase.gene, String.format("txid%d", txId), max, start);
    
//    if (taxaSet != null && taxaSet.taxons.size() > 0) {
//      return taxaSet.taxons.get(0);
//    }
    
    return taxaSet;
  }
  
  
  
  public List<EntrezGene> getGenes(Collection<Long> ids) {
    EntrezGeneSet geneSet = eutilsService.efetchGene(
        StringUtils.join(ids, ","), EntrezRetmode.xml);
    
    return geneSet.genes;
  }
  
  public EntrezTaxon getTaxonomy(long id) {
    EntrezTaxaSet taxaSet = eutilsService.efetchTaxonomy(Long.toString(id), 
                                                         EntrezRetmode.xml);
    
    if (taxaSet != null && taxaSet.taxons.size() > 0) {
      return taxaSet.taxons.get(0);
    }
    
    return null;
  }
  
  public List<EntrezTaxon> getTaxonomy(Collection<Long> ids) {
    EntrezTaxaSet taxaSet = eutilsService.efetchTaxonomy(
        StringUtils.join(ids, ","), EntrezRetmode.xml);
    
    return taxaSet.taxons;
  }
}
