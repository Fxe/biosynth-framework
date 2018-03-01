package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface EnsemblApiService {
  
  /**
   * http://rest.ensembl.org/lookup/id/ENSG00000157764?expand=0
   * 
   * @param entry
   * @param expand
   * @return
   */
  @GET("/lookup/id/{entry}")
  public Map<String, Object> getId(@Path("entry") String entry, @Query("expand") Integer expand);

  @GET("/info/data")
  public Map<String, Object> getInfoData();
  
  @GET("/info/species")
  public Map<String, Object> getInfoSpecies();
}
