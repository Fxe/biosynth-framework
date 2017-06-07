package pt.uminho.sysbio.biosynthframework.biodb.eutils;

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

  //esearch.fcgi?db=gene&term=txid326442&retstart=38&retmax=100
  @GET("/esearch.fcgi")
  public EntrezSearchResult esearch(@Query("db") EntrezDatabase db, 
                                    @Query("term") String term,
                                    @Query("retmax") int retmax,
                                    @Query("retstart") int retstart);
  
  @GET("/efetch.fcgi?db=nuccore")
  public EntrezGeneSet efetchNucleotide(@Query("id") String id, 
                                        @Query("retmode") EntrezRetmode retmode,
                                        @Query("retmode") String rettype);
//efetch.fcgi?db=nuccore&id=383394742&retmode=xml
}
