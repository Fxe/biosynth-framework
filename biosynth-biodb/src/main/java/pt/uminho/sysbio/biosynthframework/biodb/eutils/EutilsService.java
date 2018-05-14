package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Query;

public interface EutilsService {
  
  //efetch.fcgi?db=taxonomy&id=9913,30521,4932&retmode=xml
  @GET("/efetch.fcgi")
  public Object efetch(@Query("db") EntrezDatabase db, 
                       @Query("id") String id, 
                       @Query("retmode") EntrezRetmode retmode);
  
  @GET("/efetch.fcgi")
  public Object efetch(@Query("db") EntrezDatabase db, 
                       @Query("id") String id, 
                       @Query("rettype") String rettype, 
                       @Query("retmode") EntrezRetmode retmode);
  
  @GET("/efetch.fcgi?db=taxonomy")
  public EntrezTaxaSet efetchTaxonomy(@Query("id") String id, 
                                      @Query("retmode") EntrezRetmode retmode);
  
  @GET("/efetch.fcgi?db=gene")
  public EntrezGeneSet efetchGene(@Query("id") String id, 
                                  @Query("retmode") EntrezRetmode retmode);
  
  @GET("/efetch.fcgi?db=protein")
  public EntrezProtein efetchProtein(@Query("id") String id, 
                                     @Query("retmode") EntrezRetmode retmode);

  @GET("/efetch.fcgi?db=pubmed")
  public EutilsPubmedObject efetchPubmed(@Query("id") String id, 
                                         @Query("retmode") String retmode);
  

  
  @GET("/efetch.fcgi?db=nuccore")
  public EntrezGeneSet efetchNucleotide(@Query("id") String id, 
                                        @Query("retmode") EntrezRetmode retmode,
                                        @Query("rettype") String rettype);
  
  //esearch.fcgi?db=gene&term=txid326442&retstart=38&retmax=100
  @GET("/esearch.fcgi")
  public EntrezSearchResult esearch(@Query("db") EntrezDatabase db, 
                                    @Query("term") String term,
                                    @Query("retmax") int retmax,
                                    @Query("retstart") int retstart);
  
  @GET("/esearch.fcgi")
  public Map<String, Object> esearch(@Query("db") EntrezDatabase db, 
                                     @Query("term") String term,
                                     @Query("retmax") int retmax,
                                     @Query("retstart") int retstart,
                                     @Query("retmode") EntrezRetmode retmode);
  
  //esummary.fcgi?db=assembly&id=291138,223058&retmode=json
  @GET("/esummary.fcgi")
  public Map<String, Object> esummary(@Query("db") String db, 
                                      @Query("id") String id,
                                      @Query("retmode") EntrezRetmode retmode);
//efetch.fcgi?db=nuccore&id=383394742&retmode=xml
}
