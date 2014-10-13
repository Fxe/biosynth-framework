package pt.uminho.sysbio.biosynth.integration.unichem;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

public interface UnichemApi {
	//https://www.ebi.ac.uk/unichem/rest/sources/1
		
	//https://www.ebi.ac.uk/unichem/rest/src_ids/
	
	@Headers(value={"Accept: text/javascript"})
	@GET(value="/src_ids/")
	public List<Map<String, String>> getSourceIds();
	
//	https://www.ebi.ac.uk/unichem/rest/inchikey/AAOVKJBEBIDNHE-UHFFFAOYSA-N
	/**
	 * Obtain a list of all src_compound_ids (from all sources) which 
	 * are CURRENTLY assigned to a query InChIKey
	 * 
	 * @param inchiKey
	 * @return
	 */
	@Headers(value={"Accept: text/javascript"})
	@GET(value="/inchikey/{INCHI}")
	public List<Map<String, String>> getFunc1(@Path("INCHI") String inchiKey);
}
