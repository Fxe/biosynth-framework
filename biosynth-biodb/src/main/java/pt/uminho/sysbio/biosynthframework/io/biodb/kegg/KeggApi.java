package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface KeggApi {
	/**
	 * http://rest.kegg.jp/info/&lt;database&gt;
	 * 
	 * @param database = pathway | brite | module | ko | genome | &lt;org&gt; | compound | glycan |
	 * reaction | rpair | rclass | enzyme | disease | drug | dgroup | environ |
	 * genomes | genes | ligand | kegg<br>
	 * &lt;org&gt; = KEGG organism code or T number
	 * @return
	 */
//	@Headers("Content-Type: text/plain")
//	@Header(value="Content-Type: text/plain")
	@GET(value="/info/{database}")
	public String getInfo(@Path("database") String database);
	
	/**
	 * http://rest.kegg.jp/list/&lt;database&gt;
	 * 
	 * @return
	 */
	public String listDatabase(String database);
	
	
	/**
	 * http://rest.kegg.jp/list/&lt;database&gt;/&lt;org&gt;
	 * 
	 * @param database = pathway | module
	 * @param org = KEGG organism code
	 * @return
	 */
	public String listDatabase2(String database, String org);
}
