package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
