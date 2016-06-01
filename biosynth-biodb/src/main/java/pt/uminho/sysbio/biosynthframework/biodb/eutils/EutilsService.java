package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import retrofit.http.GET;
import retrofit.http.Query;

public interface EutilsService {
  
  //efetch.fcgi?db=taxonomy&id=9913,30521,4932&retmode=xml
  @GET("/efetch.fcgi")
  public Object efetch(@Query("db") EntrezDatabase db, 
                       @Query("id") String id, 
                       @Query("retmode") EntrezRetmode retmode);
  
  @GET("/efetch.fcgi?db=taxonomy")
  public EntrezTaxaSet efetchTaxonomy(@Query("id") String id, 
                                      @Query("retmode") EntrezRetmode retmode);
}
