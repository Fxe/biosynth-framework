package pt.uminho.sysbio.biosynthframework.io.biodb;

import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsApiMetabolite;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LipidmapsApiService {
//  http://www.lipidmaps.org/rest/compound/lm_id/LMGP04010986/all/
  @GET("/compound/lm_id/{entry}/all/")
  public LipidmapsApiMetabolite getMetaboliteByEntry(@Path("entry") String entry);
//  compound/abbrev/SQDG(14:0_18:1(9Z))/structure
  @GET("/compound/lm_id/{entry}/molfile/text")
  public String getMetaboliteMolFileByEntry(@Path("entry") String entry);
}
