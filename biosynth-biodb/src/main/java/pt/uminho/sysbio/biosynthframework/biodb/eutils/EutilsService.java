package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//import retrofit.http.GET;
//import retrofit.http.Query;

public interface EutilsService {
  
  //efetch.fcgi?db=taxonomy&id=9913,30521,4932&retmode=xml
  @GET("entrez/eutils/efetch.fcgi")
  public Call<Object> efetch(@Query("db") EntrezDatabase db, 
                       @Query("id") String id, 
                       @Query("retmode") EntrezRetmode retmode);
  
  @GET("entrez/eutils/efetch.fcgi")
  public Call<Object> efetch(@Query("db") EntrezDatabase db, 
                       @Query("id") String id, 
                       @Query("rettype") String rettype, 
                       @Query("retmode") EntrezRetmode retmode);
  
  @GET("entrez/eutils/efetch.fcgi?db=taxonomy")
  public Call<EntrezTaxaSet> efetchTaxonomy(@Query("id") String id, 
                                      @Query("retmode") EntrezRetmode retmode);
  
  @GET("entrez/eutils/efetch.fcgi?db=gene")
  public Call<EntrezGeneSet> efetchGene(@Query("id") String id, 
                                  @Query("retmode") EntrezRetmode retmode);
  
  @GET("entrez/eutils/efetch.fcgi?db=protein")
  public Call<EntrezProtein> efetchProtein(@Query("id") String id, 
                                     @Query("retmode") EntrezRetmode retmode);

  @GET("entrez/eutils/efetch.fcgi?db=pubmed")
  public Call<EutilsPubmedObject> efetchPubmed(@Query("id") String id, 
                                         @Query("retmode") String retmode);
  

  
  @GET("entrez/eutils/efetch.fcgi?db=nuccore")
  public Call<EntrezGeneSet> efetchNucleotide(@Query("id") String id, 
                                        @Query("retmode") EntrezRetmode retmode,
                                        @Query("rettype") String rettype);
  
  //esearch.fcgi?db=gene&term=txid326442&retstart=38&retmax=100
  @GET("entrez/eutils/esearch.fcgi")
  public Call<EntrezSearchResult> esearch(@Query("db") EntrezDatabase db, 
                                    @Query("term") String term,
                                    @Query("retmax") int retmax,
                                    @Query("retstart") int retstart);
  
  @GET("entrez/eutils/esearch.fcgi")
  public Call<Map<String, Object>> esearch(@Query("db") EntrezDatabase db, 
                                           @Query("term") String term,
                                           @Query("retmax") int retmax,
                                           @Query("retstart") int retstart,
                                           @Query("retmode") EntrezRetmode retmode);
  
  //esummary.fcgi?db=assembly&id=291138,223058&retmode=json
  @GET("entrez/eutils/esummary.fcgi")
  public Call<Map<String, Object>> esummary(@Query("db") String db, 
                                      @Query("id") String id,
                                      @Query("retmode") EntrezRetmode retmode);
//efetch.fcgi?db=nuccore&id=383394742&retmode=xml
}
