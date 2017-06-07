package pt.uminho.sysbio.biosynthframework.io.biodb;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ApiReaction;
import retrofit.http.GET;
import retrofit.http.Path;

public interface Bigg2ApiService {

  //curl 'http://bigg.ucsd.edu/api/v2/universal/reactions/ADA'
  @GET("/universal/reactions/{entry}")
  public Bigg2ApiReaction getUniversalReactionByEntry(@Path("entry") String entry);
  
  //curl 'http://bigg.ucsd.edu/api/v2/universal/reactions'
  @GET("/universal/reactions")
  public Object listUniversalReactions();
}
