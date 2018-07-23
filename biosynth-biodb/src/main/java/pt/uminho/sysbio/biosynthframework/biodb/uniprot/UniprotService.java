package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UniprotService {
  
//http://www.uniprot.org/uniprot/P12345.xml
  @GET("/uniprot/{entry}")
  public UniprotResult getByEntry(@Path("entry") String entry);
  
  //http://www.uniprot.org/uniprot/?
  //query=reviewed:yes+AND+organism:379731&format=xml&columns=id
  @GET("/uniprot/")
  public UniprotResult query(@Query("query") String query, @Query("format") String format);
  
  @GET("/uniprot/{entry}")
  public Object getByEntry_(@Path("entry") String entry);
  //proteome:UP000001425
}
