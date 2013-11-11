package edu.uminho.biosynth.core.data.io.parser.biocyc;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uminho.biosynth.core.data.io.parser.ICompoundParser;

public class BioCycMetaboliteXMLParser extends AbstractBioCycXMLParser implements ICompoundParser {

	private final static Logger LOGGER = Logger.getLogger(BioCycMetaboliteXMLParser.class.getName());
	
	private final JSONObject base;
	private final BioCycEntityType entityType;
	
	public BioCycMetaboliteXMLParser(String xmlDocument) throws JSONException {
		super(xmlDocument);
		
		this.parseContent();
		
		JSONObject jsMetabolite = null;
		if (this.content.getJSONObject("ptools-xml").has("Compound")) {
			jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("Compound");
			this.entityType = BioCycEntityType.Compound;
		} else if ( this.content.getJSONObject("ptools-xml").has("Protein")) {
			jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("Protein");
			this.entityType = BioCycEntityType.Protein;
		} else if ( this.content.getJSONObject("ptools-xml").has("RNA")) {
			jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("RNA");
			this.entityType = BioCycEntityType.RNA;
		} else {
			this.entityType = BioCycEntityType.ERROR;
			LOGGER.log(Level.SEVERE, this.content.getJSONObject("ptools-xml").toString());
		}
		
		this.base = jsMetabolite;

	}
	
	public boolean isValid() {
		return this.base != null;
	}
	
	public Set<String> getReactions() {
		try {
			Set<String> rxnIdList = null;
			JSONArray rxnJSArray = null;
			
			
			rxnIdList = new HashSet<String>();
			
			if (base.has("appears-in-right-side-of")) {
				Object rightSide = base.getJSONObject("appears-in-right-side-of").get("Reaction");
				if (rightSide instanceof JSONArray) {
					rxnJSArray = (JSONArray) rightSide;
				} else {
					rxnJSArray = new JSONArray();
					rxnJSArray.put(rightSide);
				}
				for (int i = 0; i < rxnJSArray.length(); i++) {
					rxnIdList.add( rxnJSArray.getJSONObject(i).getString("frameid"));
				}
			}
			
			if (base.has("appears-in-left-side-of")) {
				Object leftSide = base.getJSONObject("appears-in-left-side-of").get("Reaction");
				if (leftSide instanceof JSONArray) {
					rxnJSArray = (JSONArray) leftSide;
				} else {
					rxnJSArray = new JSONArray();
					rxnJSArray.put(leftSide);
				}
				for (int i = 0; i < rxnJSArray.length(); i++) {
					rxnIdList.add( rxnJSArray.getJSONObject(i).getString("frameid"));
					//System.out.println(rxnJSArray.getJSONObject(i).getString("frameid"));
				}
			}
			
			return rxnIdList;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	public String getEntry() {
		try {
			return this.base.getString("frameid");
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	public String getName() {
		try {
			String commonName;
			if (this.base.has("common-name")) {
				commonName = this.base.getJSONObject("common-name").getString("content");
			} else {
				commonName = this.getEntry();
			}
			
			return commonName;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	public String getFormula() {
		try {
			String formula = "";
			
			switch (entityType) {
				case Compound:
					if (this.base.has("cml")) {
						formula = this.base.getJSONObject("cml")
						.getJSONObject("molecule").getJSONObject("formula").getString("concise");
						formula = formula.replaceAll(" ", "");
					}
					break;
				default:
					break;
			}
			
			return formula;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	public String getEntityClass() {
		return this.entityType.toString();
	}
	
	public String getComment() {

		return null;
	}
	
	public String getSpecies() {
		try {
			String ret = "";
			try {
				ret = this.base.getJSONObject("species").getJSONObject("Organism").getString("frameid");
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "EXCEPTION ! " + this.xmlDocument);
				throw e;
			}
			return ret;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}

	@Override
	public String getRemark() {
		try {
			String keggLink = "";
			if ( this.base.has("dblink") ) {
				JSONArray jsonArray = toJSONArray(this.base.get("dblink"));
				for (int i = 0; i < jsonArray.length(); i++) {
					if ( jsonArray.getJSONObject(i).getString("dblink-db").equals("LIGAND-CPD")) {
						keggLink = jsonArray.getJSONObject(i).get("dblink-oid").toString();
					}
				}
			}
			return keggLink;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	private static JSONArray toJSONArray(Object obj) {
		if ( obj instanceof JSONArray) return (JSONArray) obj ;
		JSONArray jsArray = new JSONArray();
		jsArray.put(obj);
		
		return jsArray;
	}

	@Override
	public Set<String> getSimilarity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getEnzymes() {
		// TODO Auto-generated method stub
		return null;
	}
}
