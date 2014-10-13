package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;

public class AbstractBiocycDao {
	protected String pgdb = "META";
	
	protected final static String xmlGet = "http://biocyc.org/getxml?%s:%s";
	protected final String xmlquery = "http://biocyc.org/xmlquery?%s";
	
	public Set<String> getAllBioCycPGDB() {
		try {
			//TODO: apply local caching if enabled
			//String xmlDoc = edu.uminho.biosynth.util.BioSynthUtilsIO.readFromFile("./input/xml/xmlqueryDBS.xml"); 
			String xmlDoc = HttpRequest.get(xmlquery + "dbs");
			if ( xmlDoc == null) {
//				LOGGER.error("Error Retrieve - pgdbs");
				return null;
			}
			
			JSONObject obj = XML.toJSONObject(xmlDoc).getJSONObject("ptools-xml");
			JSONObject metaData = obj.getJSONObject("metadata");
			Set<String> pgdbs = new HashSet<String> ();
			
			JSONArray pgdbsArray;
			Object pgdbsObj = metaData.get("PGDB");
			if ( pgdbsObj instanceof JSONArray) {
				pgdbsArray = (JSONArray) pgdbsObj;
			} else {
				pgdbsArray = new JSONArray ();
				pgdbsArray.put(pgdbsObj);
			}
			for (int i = 0; i < pgdbsArray.length(); i++) {
				pgdbs.add( pgdbsArray.getJSONObject(i).getString("orgid"));
			}
			//System.out.println(obj);
			return pgdbs;
		} catch (JSONException e) {
//			LOGGER.error(String.format("JSONException - %s", e.getMessage()));
			return null;
		}
	}
	
	public String getPgdb() { return pgdb;}
	public void setPgdb(String pgdb) { this.pgdb = pgdb;}
}
