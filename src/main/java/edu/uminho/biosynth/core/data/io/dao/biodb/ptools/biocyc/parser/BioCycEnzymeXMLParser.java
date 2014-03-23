package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BioCycEnzymeXMLParser extends AbstractBioCycXMLParser {
	
	private final static Logger LOGGER = Logger.getLogger(BioCycEnzymeXMLParser.class.getName());

	//private Map<String, String> genes;
	private final JSONArray base;
	//private final BioCycEntityType entityType;
	
	public BioCycEnzymeXMLParser(String xmlDocument) throws JSONException {
		super(xmlDocument);
		
		this.parseContent();
		
//		String[] unwantedTags = {"xml:base", "ptools-version", "metadata"};
//		@SuppressWarnings("unchecked")
//		Set<String> tags = new HashSet<>( this.content.getJSONObject("ptools-xml").keySet());
////	System.out.println(this.content.getJSONObject("ptools-xml").keySet());
//		tags.removeAll( Arrays.asList(unwantedTags));
		
		if ( !this.content.getJSONObject("ptools-xml").has("Protein")) {
			if (!(this.content.getJSONObject("ptools-xml").getJSONObject("metadata").getInt("num_results") == 0)) {
				LOGGER.log(Level.SEVERE, "");
			}
//			
			this.base = null;
		} else {
			
			Object genes = this.content.getJSONObject("ptools-xml").get("Protein");
			if ( genes instanceof JSONArray) {
				base = (JSONArray) genes;
			} else {
				base = new JSONArray();
				base.put( genes);
			}
		}
		
//	System.out.println(base.getJSONObject(0).get("species"));
//	this.parseGenes();
	}
	
	public boolean isValid() {
		return this.base != null;
	}
	
	@Deprecated
	public void parseGenes() throws JSONException {
		if (this.base == null) {
			System.out.println("No organism info");
			return;
		}
		for (int i = 0; i < this.base.length(); i++) {
			System.out.println("ID\t" + this.getEntry(i));
			System.out.println("NAME\t" + this.getName(i));
//			System.out.println("P\t" + this.getProduct(i));
			System.out.println("S\t" + this.getOrganism(i));
		}
	}
	
	public int numberOfGenes() {
		return this.base.length();
	}
	
	public String getEntry(int index) throws JSONException {
		JSONObject gene = this.base.getJSONObject(index);
		return gene.getString("frameid");
	}
	
	public String getName(int index) throws JSONException {
		JSONObject gene = this.base.getJSONObject(index);
		String name = "";
		if (gene.has("common-name")) {
			name = gene.getJSONObject("common-name").getString("content");
		}
		return name;
	}
	
	public List<String> getProduct(int index) throws JSONException {
		JSONObject gene = this.base.getJSONObject(index);
		Object product = gene.getJSONObject("product").get("Protein");
		JSONArray productArray;
		if (product instanceof JSONArray) {
			productArray = (JSONArray) product;
		} else {
			productArray = new JSONArray();
			productArray.put( product);
		}
		List<String> proteinList = new ArrayList<>();
		for (int i = 0; i < productArray.length(); i++) {
			proteinList.add( productArray.getJSONObject(i).getString("frameid"));
		}

		return proteinList;
	}
	
	public Map<String, String> getOrganism(int index) throws JSONException {
		Map<String, String> ret = new HashMap<>();
		
		Object speciesObj = this.base.getJSONObject(index).get("species");
		JSONArray jsSpecies;
		if ( speciesObj instanceof JSONArray) {
			jsSpecies = (JSONArray) speciesObj;
		} else {
			jsSpecies = new JSONArray();
			jsSpecies.put(speciesObj);
		}
		
		for (int i = 0; i < jsSpecies.length(); i++) {
			ret.put(jsSpecies.getJSONObject(i).getJSONObject("Organism").getString("frameid"), this.getEntry(index));
		}
		
//		List<String> proteinList = this.getProduct(index);
//		for (String proteinId : proteinList) {
//			String proteinXML = HttpRequest.get("http://biocyc.org/getxml?META:" + proteinId);
//			if ( proteinXML == null)  {
//				LOGGER.log(Level.SEVERE, "Error HttpGet - " + proteinId);
//				return null;
//			}
//			BioCycMetaboliteXMLParser parser = new BioCycMetaboliteXMLParser(proteinXML);
//			ret.put(proteinId, parser.getSpecies());
//		}
		
		return ret;
	}
}
