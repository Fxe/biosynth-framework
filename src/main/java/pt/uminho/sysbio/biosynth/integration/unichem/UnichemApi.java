package pt.uminho.sysbio.biosynth.integration.unichem;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Headers;

public interface UnichemApi {
	//https://www.ebi.ac.uk/unichem/rest/sources/1
		
	//https://www.ebi.ac.uk/unichem/rest/src_ids/
	
	@Headers(value={"Accept: text/javascript"})
	@GET(value="/src_ids/")
	public List<Map<String, String>> getSourceIds();
}
