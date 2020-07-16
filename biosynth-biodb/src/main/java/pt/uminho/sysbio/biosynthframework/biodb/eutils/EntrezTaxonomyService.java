package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.io.IOError;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.lf5.LogLevel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class EntrezTaxonomyService {
  public EutilsService eutilsService;

  public EntrezTaxonomyService() {
    
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
                                              .build();
    eutilsService = retrofit.create(EutilsService.class);
  }
  
  public Object fetch(EntrezDatabase db, String id) {
    return eutilsService.efetch(db, id, EntrezRetmode.xml);
  }
  public Object fetch(EntrezDatabase db, long id) {
    return eutilsService.efetch(db, Long.toString(id), EntrezRetmode.xml);
  }
  
  public EntrezProtein getProtein(String id) {
    EntrezProtein proteinSet = null;
    try {
      proteinSet = eutilsService.efetchProtein(id, EntrezRetmode.xml).execute().body();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(proteinSet);
    return null;
  }
  
  public EntrezSearchResult searchGenes(long txId, int max, int start) {
    EntrezSearchResult taxaSet = null;
    try {
      taxaSet = eutilsService.esearch(
          EntrezDatabase.gene, String.format("txid%d", txId), max, start).execute().body();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
//    if (taxaSet != null && taxaSet.taxons.size() > 0) {
//      return taxaSet.taxons.get(0);
//    }
    
    return taxaSet;
  }
  
  
  
  public List<EntrezGene> getGenes(Collection<Long> ids) {
    EntrezGeneSet geneSet = null;
    try {
      geneSet = eutilsService.efetchGene(
          StringUtils.join(ids, ","), EntrezRetmode.xml).execute().body();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return geneSet.genes;
  }
  
  public EntrezTaxon getTaxonomy(long id) {
    EntrezTaxaSet taxaSet = null;
    try {
    taxaSet = eutilsService.efetchTaxonomy(Long.toString(id), 
                                                         EntrezRetmode.xml).execute().body();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (taxaSet != null && taxaSet.taxons.size() > 0) {
      return taxaSet.taxons.get(0);
    }
    
    return null;
  }
  
  public List<EntrezTaxon> getTaxonomy(Collection<Long> ids) {
    EntrezTaxaSet taxaSet = null;
    try {
    taxaSet = eutilsService.efetchTaxonomy(
        StringUtils.join(ids, ","), EntrezRetmode.xml).execute().body();
  } catch (IOException e) {
    e.printStackTrace();
  }
    return taxaSet.taxons;
  }
}
