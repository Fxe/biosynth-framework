package edu.uminho.biosynth.core.data.io.parser.biocyc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.parser.IGenericMetaboliteParser;

public class BioCycMetaboliteXMLParser extends AbstractBioCycXMLParser implements IGenericMetaboliteParser {

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
	
	public List<String> getReactions() {
		try {
			List<String> rxnIdList = null;
			JSONArray rxnJSArray = null;
			
			
			rxnIdList = new ArrayList<String>();
			
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
	
	public String getSource() {
		try {
			return this.base.getString("orgid");
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException " + e.getMessage());
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
		String formula = null;
		
		try {
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
			LOGGER.log(Level.SEVERE, "JSONException" + ex.getMessage());
			return null;
		}
	}
	
	public String getInchi() {
		String inchi = null;
		try {
			if (this.base.has("inchi")) {
				inchi = this.base.getJSONObject("inchi").getString("content");
				inchi = inchi.replace("InChI=", "");
			}
			
			return inchi;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException " + ex.getMessage());
			return null;
		}
	}
	
	public Integer getCharge() {
		try {
			Integer charge = null;
			
			switch (entityType) {
				case Compound:
					if (this.base.has("cml")) {
						charge = this.base.getJSONObject("cml").getJSONObject("molecule").getInt("formalCharge");
					}
					break;
				default:
					break;
			}
			
			return charge;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException" + ex.getMessage());
			return null;
		}
	}
	
	public String getSmiles() {
		try {
//			System.out.println(this.base.getJSONObject("cml").getJSONObject("molecule").get("string"));
			String smiles = "";
			
			switch (entityType) {
				case Compound:
					if (this.base.has("cml")) {
						Object jsStringObj = this.base.getJSONObject("cml")
								.getJSONObject("molecule").get("string");
						JSONArray jsArrayString;
						if (jsStringObj instanceof JSONArray) {
							jsArrayString = (JSONArray) jsStringObj;
						} else {
							jsArrayString = new JSONArray();
							jsArrayString.put(jsStringObj);
						}
						for (int i = 0; i < jsArrayString.length(); i++) {
							JSONObject jsArrObj = jsArrayString.getJSONObject(i);
							if (jsArrObj.getString("title").equals("smiles")) {
								smiles = jsArrObj.getString("content");
							}
						}
						
						
						smiles = smiles.replaceAll(" ", "");
					}
					break;
				default:
					break;
			}
			
			return smiles;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException" + ex.getMessage() + " => " + this.getEntry());
			return null;
		}
	}
	
	public String getEntityClass() {
		return this.entityType.toString();
	}
	
	public String getComment() {
		String comment = null;
		
		try {
			if (this.base.has("comment")) {
				comment = "";
				JSONArray jsArrayComment = this.getObjectAsArray(this.base, "comment");
				for (int i = 0; i < jsArrayComment.length(); i++) {
					JSONObject commentJs = jsArrayComment.getJSONObject(i);
					if (commentJs.has("content")) {
						comment.concat(commentJs.getString("content"));
					}
				}
			}
			return comment;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException " + e.getMessage() + " => " + this.getEntry());
			return null;
		}
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

	public Double getMolWeight() {
		try {
			Double molWeight = null;
			if (this.base.has("molecular-weight")) {
				molWeight = this.base.getJSONObject("molecular-weight").getDouble("content");
			}
			
			return molWeight;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException" + e.getMessage());
			return null;
		}
	}
	
	public Double getCmlMolWeight() {
		try {
			Double cmlMolWeight = null;
			
			switch (entityType) {
				case Compound: //Must have cml -> molecule -> float
					if (this.base.has("cml") && this.base.getJSONObject("cml").has("molecule")
							&& this.base.getJSONObject("cml").getJSONObject("molecule").has("float") ) {
						cmlMolWeight = this.base.getJSONObject("cml")
						.getJSONObject("molecule").getJSONObject("float").getDouble("content");
					}
					break;
				default:
					break;
			}
			
			return cmlMolWeight;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException" + ex.getMessage());
			return null;
		}
	}
	
	public Double getGibbs() {
		try {
			Double gibbs = null;
			if (this.base.has("gibbs-0")) {
				gibbs = this.base.getJSONObject("gibbs-0").getDouble("content");
			}
			
			return gibbs;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException" + e.getMessage());
			return null;
		}
	}
	
	public List<String> getSynonym() {
		try {
			List<String> synonyms = new ArrayList<> ();
			if (this.base.has("synonym")) {
				JSONArray synJsArray = null;
				Object synObj = this.base.get("synonym");
				if (synObj instanceof JSONArray) {
					synJsArray = (JSONArray) synObj;
				} else {
					synJsArray = new JSONArray();
					synJsArray.put(synObj);
				}
				for (int i = 0; i < synJsArray.length(); i++) {
					synonyms.add( synJsArray.getJSONObject(i).getString("content"));
				}
			}
			
			return synonyms;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException" + e.getMessage());
			return null;
		}
	}
	
	public List<BioCycMetaboliteCrossReferenceEntity> getCrossReferences() {
		try {
			List<BioCycMetaboliteCrossReferenceEntity> crossReferences = new ArrayList<> ();
			if (this.base.has("dblink")) {
				JSONArray dblinkJsArray = null;
				Object dblinkObj = this.base.get("dblink");
				if (dblinkObj instanceof JSONArray) {
					dblinkJsArray = (JSONArray) dblinkObj;
				} else {
					dblinkJsArray = new JSONArray();
					dblinkJsArray.put(dblinkObj);
				}
				for (int i = 0; i < dblinkJsArray.length(); i++) {
					JSONObject dblinkJsObj = dblinkJsArray.getJSONObject(i);
					BioCycMetaboliteCrossReferenceEntity crossReference = new BioCycMetaboliteCrossReferenceEntity();
					crossReference.setType(GenericCrossReference.Type.DATABASE);
					if (dblinkJsObj.has("dblink-db"))
						crossReference.setRef(dblinkJsObj.getString("dblink-db"));
					if (dblinkJsObj.has("dblink-oid"))
						crossReference.setValue(dblinkJsObj.get("dblink-oid").toString());
					if (dblinkJsObj.has("unification"))
						crossReference.setRelationship(dblinkJsObj.getString("unification"));
					if (dblinkJsObj.has("dblink-URL"))
						crossReference.setUrl(dblinkJsObj.getString("dblink-URL"));
					crossReferences.add(crossReference);
//					System.out.println(dblinkJsObj);
//					crossReferences.add( synJsArray.getJSONObject(i).getString("content"));
				}
			}
			
			return crossReferences;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException" + e.getMessage() + " => " + this.getEntry());
			return null;
		}
	}
	
	public List<String> getParents() {
		List<String> res = new ArrayList<> ();
		if (this.base.has("parent")) {
			JSONArray parentJsArray = this.getObjectAsArray(this.base, "parent");
			for (int i = 0; i < parentJsArray.length(); i++) {
				Object arrayIndexObj = parentJsArray.get(i);
				if (arrayIndexObj instanceof JSONObject) {
					JSONObject parentJsObj = (JSONObject) arrayIndexObj;
					if (parentJsObj.has("Compound")) {
						res.add(parentJsObj.getJSONObject("Compound").getString("frameid"));
					} else {
						System.out.println(parentJsArray);
						System.exit(0);
					}
				} else {
//					JSONObject js = new JSONObject();
					res.add(parentJsArray.getString(i));
				}
			}
		}
		return res;
	}

	public List<String> getInstanses() {
		List<String> res = new ArrayList<> ();
		if (this.base.has("instance")) {
			JSONArray instanceJsArray = null;
			Object instanceObj = this.base.get("instance");
			if (instanceObj instanceof JSONArray) {
				instanceJsArray = (JSONArray) instanceObj;
			} else {
				instanceJsArray = new JSONArray();
				instanceJsArray.put(instanceObj);
			}
			for (int i = 0; i < instanceJsArray.length(); i++) {
				JSONObject instanceJsObj = instanceJsArray.getJSONObject(i);
				if (instanceJsObj.has("Compound")) {
					res.add(instanceJsObj.getJSONObject("Compound").getString("frameid"));
				} else {
					System.out.println(instanceJsArray);
					System.exit(0);
				}
			}
		}
		return res;
	}
	
	public List<String> getSubclasses() {
		List<String> res = new ArrayList<> ();
		if (this.base.has("subclass")) {
			JSONArray subclassJsArray = null;
			Object subclassObj = this.base.get("subclass");
			if (subclassObj instanceof JSONArray) {
				subclassJsArray = (JSONArray) subclassObj;
			} else {
				subclassJsArray = new JSONArray();
				subclassJsArray.put(subclassObj);
			}
			for (int i = 0; i < subclassJsArray.length(); i++) {
				JSONObject subclassJsObj = subclassJsArray.getJSONObject(i);
				if (subclassJsObj.has("Compound")) {
					res.add(subclassJsObj.getJSONObject("Compound").getString("frameid"));
				} else {
					System.out.println(subclassJsArray);
					System.exit(0);
				}
			}
		}
		return res;
	}
	
	public List<String> getEnzymes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private JSONArray getObjectAsArray(JSONObject base, String key) {
		JSONArray jsArray = null;
		Object jsObject = base.get(key);
		if (jsObject instanceof JSONArray) {
			jsArray = (JSONArray) jsObject;
		} else {
			jsArray = new JSONArray();
			jsArray.put(jsObject);
		}
		return jsArray;
	}
}
