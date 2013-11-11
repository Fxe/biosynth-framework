package edu.uminho.biosynth.core.data.io.parser.biocyc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uminho.biosynth.core.data.io.parser.IGenericReactionParser;

public class BioCycReactionXMLParser extends AbstractBioCycXMLParser implements IGenericReactionParser {

	private final static Logger LOGGER = Logger.getLogger(BioCycReactionXMLParser.class.getName());
	public static boolean VERBOSE = false;

	private final JSONObject base;
	private Map<String, String> leftEq;
	private Map<String, String> rightEq;
	
	public BioCycReactionXMLParser(String xmlDocument) throws JSONException {
		super(xmlDocument);
		super.parseContent();
		this.leftEq = null;
		this.rightEq = null;
	
		JSONObject jsRxn = null;;
		try {
			jsRxn = super.content.getJSONObject("ptools-xml").getJSONObject("Reaction");
		} catch(JSONException jsEx) {
			LOGGER.log(Level.SEVERE, jsEx.getMessage());
		}
		this.base = jsRxn;
	}
	
	public int getOrientation() {
		try {
			if ( !this.base.has("reaction-direction")) return 0;
			String orientation = this.base.getString("reaction-direction");
			if ( orientation.equals("LEFT-TO-RIGHT")) return 1;
			if ( orientation.equals("REVERSIBLE")) return 0;
			if ( orientation.equals("PHYSIOL-LEFT-TO-RIGHT")) return 3;
			if ( orientation.equals("RIGHT-TO-LEFT")) return -1;
			if ( orientation.equals("PHYSIOL-RIGHT-TO-LEFT")) return -3;
			if ( orientation.equals("IRREVERSIBLE-LEFT-TO-RIGHT")) return 2;
			if ( orientation.equals("IRREVERSIBLE-RIGHT-TO-LEFT")) return -2;
			System.err.println(this.getEntry() + " => " + orientation);
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
		}
		return 9999;
	}
	
	public boolean isValid() {
		return this.base != null;
	}
	
	@Override
	public String getEntry() {
		try {
			return base.getString("frameid");		
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}

	@Override
	public String getName() {
		return this.getEntry();
	}
	
	private Map<String, String> parseEqObject(Object obj) {
		try {
			Map<String, String> retMap = new HashMap<>();
			JSONArray jsArray;
			if (obj instanceof JSONArray ) {
				jsArray = (JSONArray) obj;
			} else {
				jsArray = new JSONArray();
				jsArray.put(obj);
			}
			//System.out.println(jsArray);
			for (int i = 0; i < jsArray.length(); i++) {
				Object arrayElement = jsArray.get(i);
				
				if ( arrayElement instanceof JSONObject) {
				
					JSONObject compound = (JSONObject) arrayElement;
					String cpdId;
					if ( compound.has("Compound") ) { // {"Compound":{ ... , "frameid":"CPDID"}
						cpdId = compound.getJSONObject("Compound").getString("frameid");
					} else if ( compound.has("Protein") ) { // {"Protein":{ ... , "frameid":"CPDID"}
						cpdId = compound.getJSONObject("Protein").getString("frameid");
					} else if ( compound.has("RNA")) {
						cpdId = compound.getJSONObject("RNA").getString("frameid");
					} else if ( compound.has("content")) { // {"content":"e<SUP>-<\/SUP>"}
						cpdId = compound.getString("content");
					} else {
						System.err.println("PARSER ERROR parseEqObject - " + this.getEntry());
						cpdId = "ERROR_PARSE_OBJECT";
					}
					 
					String stoich = "1";
					if (compound.has("coefficient")) {
						try {
							stoich = compound.getJSONObject("coefficient").get("content").toString();
						} catch (JSONException jsEx) {
							
						}
					}
					retMap.put( cpdId.replaceAll(" ", "_"), stoich);
				} else {
					String unmappedId = (String) arrayElement;
					retMap.put( unmappedId.replaceAll(" ", "_"), "1");
				}
	//			System.out.println(stoich + " " + cpdId);
			}
	
			return retMap;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	private void buildEqMap() throws JSONException {
		Object left = base.get("left");
		Object right = base.get("right");
		leftEq = parseEqObject(left);
		rightEq = parseEqObject(right);
	}

	@Override
	public String getEquation() {
		try {
			if ( leftEq == null || rightEq == null) this.buildEqMap();
			
			StringBuilder sb = new StringBuilder();
			
			Iterator<String> it;
			it = leftEq.keySet().iterator();
			while ( it.hasNext()) {
				String cpdId = it.next();
				sb.append( leftEq.get(cpdId)).append(' ').append(cpdId);
				if ( it.hasNext()) sb.append(" + ");
			}
			
			
			if ( this.getOrientation() > 10) {
				sb.append(" <?> ");
			} else {
				if (this.getOrientation() > 0) {
					sb.append(" => ");
				} else if ( this.getOrientation() < 0) {
					sb.append(" <= ");
				} else {
					sb.append(" <=> ");
				}
			}
			
			it = rightEq.keySet().iterator();
			while ( it.hasNext()) {
				String cpdId = it.next();
				sb.append( rightEq.get(cpdId)).append(' ').append(cpdId);
				if ( it.hasNext()) sb.append(" + ");
			}
	
			return sb.toString();
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
				Object obj = this.base.get("dblink");
				JSONArray jsonArray;
				if ( obj instanceof JSONArray) {
					jsonArray = this.base.getJSONArray("dblink");
				} else {
					jsonArray = new JSONArray();
					jsonArray.put(obj);
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					if ( jsonArray.getJSONObject(i).getString("dblink-db").equals("LIGAND-RXN")) {
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

	@Override
	public Set<String> getSimilarReactions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRPair() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getEnzymes() {
		try {
			Set<String> ecn = new HashSet<String> ();
			if ( base.has("ec-number")) {
				JSONArray ecArr;
				Object obj = base.get("ec-number");
				if (obj instanceof JSONArray) {
					ecArr = (JSONArray) obj;
				} else {
					ecArr = new JSONArray();
					ecArr.put(obj);
				}
				//System.out.println(ecArr);
				ecn = new HashSet<>();
				for (int i = 0; i < ecArr.length(); i++) {
					//System.out.println( ecArr.get(i));
					try {
						ecn.add(ecArr.getJSONObject(i).getString("content"));
					} catch (JSONException jsEx) {
						ecn.add(ecArr.getString(i));
					}
				}
			}
			return ecn;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}

}
