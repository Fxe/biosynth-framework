package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiReaction;
import retrofit.http.GET;
import retrofit.http.Path;

public interface Bigg2ApiService {
  
  public class ListResult{
    public long results_count;
    public List<Map<String, Object>> results = new ArrayList<> ();
  }

  //curl 'http://bigg.ucsd.edu/api/v2/universal/reactions/ADA'
  @GET("/universal/reactions/{entry}")
  public Bigg2ApiReaction getUniversalReactionByEntry(@Path("entry") String entry);
  
  //curl 'http://bigg.ucsd.edu/api/v2/universal/reactions'
  @GET("/universal/reactions")
  public ListResult listUniversalReactions();
  
  //curl 'http://bigg.ucsd.edu/api/v2/universal/metabolites/g3p'
  @GET("/universal/metabolites/{entry}")
  public Bigg2ApiMetabolite getUniversalMetaboliteByEntry(@Path("entry") String entry);
  
  //curl 'http://bigg.ucsd.edu/api/v2/universal/metabolites'
  @GET("/universal/metabolites")
  public ListResult listUniversalMetabolites();
}
