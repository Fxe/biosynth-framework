package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEcNumberEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionRightEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.IGenericReactionParser;

public class BioCycReactionXMLParser  extends AbstractBioCycXMLParser 
	implements IGenericReactionParser  {

	private final static Logger LOGGER = LoggerFactory.getLogger(BioCycReactionXMLParser.class);
	public static boolean VERBOSE = false;

	private final JSONObject base;
	private Map<String, String> leftEq;
	private Map<String, String> rightEq;
	
	private JSONArray getJsonArray(JSONObject obj, String key) {
		JSONArray jsonArray;
		Object o = obj.get(key);

		if (o instanceof JSONArray) {
			jsonArray = (JSONArray) o;
		} else {
			jsonArray = new JSONArray();
			jsonArray.put(o);
		}
		
		return jsonArray;
	}
	
	private JSONObject getJsonObject(JSONObject obj, String key) {
		JSONObject jsonObject;
		Object o = obj.get(key);

		if (o instanceof JSONObject) {
			jsonObject = (JSONObject) o;
		} else {
			jsonObject = new JSONObject();
			jsonObject.put("content", o);
		}
		
		return jsonObject;
	}
	
	public BioCycReactionXMLParser(String xmlDocument) throws JSONException {
		super(xmlDocument);
		super.parseContent();
		this.leftEq = null;
		this.rightEq = null;
	
		JSONObject jsRxn = null;;
		try {
			jsRxn = super.content.getJSONObject("ptools-xml").getJSONObject("Reaction");
		} catch(JSONException jsEx) {
			LOGGER.error(jsEx.getMessage());
		}
		this.base = jsRxn;
	}
	
	public String getOrientationString() {
		if ( !this.base.has("reaction-direction")) return null;
		return this.base.getString("reaction-direction");
	}
	
	public Orientation getOrientation() throws JSONException {
		
		if ( !this.base.has("reaction-direction")) return Orientation.Unknown;
		String orientation = this.base.getString("reaction-direction");
		if ( orientation.equals("LEFT-TO-RIGHT")) return Orientation.LeftToRight;
		if ( orientation.equals("REVERSIBLE")) return Orientation.Reversible;
		if ( orientation.equals("PHYSIOL-LEFT-TO-RIGHT")) return Orientation.LeftToRight;
		if ( orientation.equals("RIGHT-TO-LEFT")) return Orientation.RightToLeft;
		if ( orientation.equals("PHYSIOL-RIGHT-TO-LEFT")) return Orientation.RightToLeft;
		if ( orientation.equals("IRREVERSIBLE-LEFT-TO-RIGHT")) return Orientation.LeftToRight;
		if ( orientation.equals("IRREVERSIBLE-RIGHT-TO-LEFT")) return Orientation.RightToLeft;
		
		return null;
	}
	
	public boolean isValid() {
		if (this.base == null) return false;
		
		if (this.base.has("error")) {
			return false;
		}
		
		return true;
	}
	
	public String getFrameId() {
		return base.getString("frameid");
	}
	
	@Override
	public String getEntry() {
		return base.getString("ID");
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
			LOGGER.error(ex.getMessage());
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
			
			
			if ( this.getOrientation().equals(Orientation.Unknown)) {
				sb.append(" <?> ");
			} else {
				if (this.getOrientation().equals(Orientation.LeftToRight)) {
					sb.append(" => ");
				} else if ( this.getOrientation().equals(Orientation.RightToLeft)) {
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
			LOGGER.error(ex.getMessage());
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
			LOGGER.error(ex.getMessage());
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
			LOGGER.error(ex.getMessage());
			return null;
		}
	}
	
	public List<BioCycReactionEcNumberEntity> getEcNumbers() {
		List<BioCycReactionEcNumberEntity> res = new ArrayList<> ();
		if ( base.has("ec-number")) {
			
			JSONObject ecnJsonObject = this.getJsonObject(this.base, "ec-number");
			
			LOGGER.debug("ecnJsonObject: " + ecnJsonObject);
			
			JSONArray ecnJsonArray = this.getJsonArray(ecnJsonObject, "content");
			for (int i = 0; i < ecnJsonArray.length(); i++) {
				
				BioCycReactionEcNumberEntity ecn = new BioCycReactionEcNumberEntity();
				Object o = ecnJsonArray.get(i);
				if (o instanceof JSONObject) {
					JSONObject ecnInnerJsonObject = (JSONObject) o;
					ecn.setEcNumber(ecnInnerJsonObject.getString("content"));
					String official = ecnInnerJsonObject.getString("official");
					if (official.trim().toLowerCase().equals("t")) {
						ecn.setOfficial(true);
					} else {
						LOGGER.warn("UNABLE TO PARSE" + ecnInnerJsonObject);
					}
				} else {
					ecn.setEcNumber((String) o);
				}
				res.add(ecn);
			}
		}
		
		return res;
	}
	
	public Boolean isPhysiologicallyRelevant() {
		
		if ( base.has("physiologically-relevant")) {
			JSONObject physioJsonObject = this.base.getJSONObject("physiologically-relevant");
			
			LOGGER.debug("physiologically-relevant: " + physioJsonObject);
			
			Boolean physio = physioJsonObject.getBoolean("content");
			return physio;
		}
		
		return null;
	}
	
	public Map<String, String> getStoichiometry(Object stoichObj) {
		Map<String, String> properties = new HashMap<> ();
		properties.put("cpdEntry", null);
		properties.put("coefficient", "1");
		properties.put("compartment", null);
		
		
		if (stoichObj instanceof JSONObject) {
			JSONObject stoichiometryJsonObject = (JSONObject) stoichObj;
			String type = null;
			Iterator<?> iterator = stoichiometryJsonObject.keys();
			while (iterator.hasNext()) {
				Object entityType = iterator.next();
				type = entityType.toString();
				
				LOGGER.debug("Type: " + type);
				
				if (type.equals("compartment")) {
					JSONObject jsonObject = stoichiometryJsonObject.getJSONObject(type).getJSONObject("cco");
					String frameId = jsonObject.getString("frameid");
					String orgId = jsonObject.getString("orgid");
					properties.put("compartment", String.format("%s:%s", orgId, frameId));
//					System.out.println(jsonObject.getJSONObject("cco"));
				} else if (type.equals("coefficient")) {
					JSONObject coefficientJsonObject = this.getJsonObject(stoichiometryJsonObject, "coefficient");
					String coefficient = coefficientJsonObject.get("content").toString();
					properties.put("coefficient", coefficient);
				} else if (type.equals("Compound") || type.equals("Protein") || type.equals("RNA")) {
					JSONObject jsonObject = stoichiometryJsonObject.getJSONObject(type);
					String frameId = jsonObject.getString("frameid");
					String orgId = jsonObject.getString("orgid");
					properties.put("cpdEntry", String.format("%s:%s", orgId, frameId));
				} else if (type.equals("content")) {
					String cpdEntry = stoichiometryJsonObject.getString("content");
					properties.put("cpdEntry", cpdEntry);
				} else if (type.equals("name-slot")) {
					properties.put("coefficient", stoichiometryJsonObject.getString("name-slot"));
				} else if (type.equals("n-name")) {
					properties.put("coefficient", stoichiometryJsonObject.getJSONObject("n-name").getString("content"));
				} else if (type.equals("n-1-name")) {
					properties.put("coefficient", stoichiometryJsonObject.getJSONObject("n-1-name").getString("content"));
				} else {
					LOGGER.warn(String.format("[%s] unknown stoichiometry type [%s]", this.getEntry(), stoichObj));
				}
			}
		} else {
			String cpdEntry = (String) stoichObj;
			properties.put("cpdEntry", cpdEntry);
		}
		LOGGER.debug("Stoichiometry properties: " + properties);
		return properties;
	}
	
	public List<BioCycReactionLeftEntity> getLeft() throws JSONException {
		List<BioCycReactionLeftEntity> leftEntities = new ArrayList<> ();
		
		if ( base.has("left")) {
			JSONArray leftJsonArray = this.getJsonArray(this.base, "left");
			
			for (int i = 0; i < leftJsonArray.length(); i++) {
				Map<String, String> properties = this.getStoichiometry(leftJsonArray.get(i));
				
				BioCycReactionLeftEntity bioCycReactionLeftEntity = new BioCycReactionLeftEntity();
				try {
					bioCycReactionLeftEntity.setStoichiometry(Double.parseDouble(properties.get("coefficient")));
					bioCycReactionLeftEntity.setCompartment(properties.get("compartment"));
				} catch (NumberFormatException e) {
					bioCycReactionLeftEntity.setStoichiometry(-1);
				}
				bioCycReactionLeftEntity.setCoefficient(properties.get("coefficient"));
				bioCycReactionLeftEntity.setCpdEntry(properties.get("cpdEntry"));
				leftEntities.add(bioCycReactionLeftEntity);
			}
		}
		
		return leftEntities;
	}
	
	public List<BioCycReactionRightEntity> getRight() throws JSONException {
		List<BioCycReactionRightEntity> rightEntities = new ArrayList<> ();
		
		if ( base.has("right")) {
			JSONArray rightJsonArray = this.getJsonArray(this.base, "right");
			for (int i = 0; i < rightJsonArray.length(); i++) {
				Map<String, String> properties = this.getStoichiometry(rightJsonArray.get(i));
//				System.out.println(properties);
				BioCycReactionRightEntity bioCycReactionRightEntity = new BioCycReactionRightEntity();
				try {
					bioCycReactionRightEntity.setStoichiometry(Double.parseDouble(properties.get("coefficient")));
					bioCycReactionRightEntity.setCompartment(properties.get("compartment"));
				} catch (NumberFormatException e) {
					bioCycReactionRightEntity.setStoichiometry(-1);
				}
				bioCycReactionRightEntity.setCoefficient(properties.get("coefficient"));
				bioCycReactionRightEntity.setCpdEntry(properties.get("cpdEntry"));
				rightEntities.add(bioCycReactionRightEntity);
			}
		}
		
		return rightEntities;
	}
	
	public List<BioCycReactionCrossReferenceEntity> getCrossReferences() throws JSONException {
		List<BioCycReactionCrossReferenceEntity> crossReferences = new ArrayList<> ();
		
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
				BioCycReactionCrossReferenceEntity crossReference = new BioCycReactionCrossReferenceEntity();

				if (dblinkJsObj.has("dblink-db"))
					crossReference.setRef(dblinkJsObj.getString("dblink-db"));
				if (dblinkJsObj.has("dblink-oid"))
					crossReference.setValue(dblinkJsObj.get("dblink-oid").toString());
				if (dblinkJsObj.has("unification"))
					crossReference.setRelationship(dblinkJsObj.getString("unification"));
				if (dblinkJsObj.has("dblink-URL"))
					crossReference.setUrl(dblinkJsObj.getString("dblink-URL"));
				
				if (crossReference.getRef().equals("UNIPROT")) {
					crossReference.setType(ReferenceType.GENE);
				} else {
					crossReference.setType(ReferenceType.DATABASE);
				}
				
				crossReferences.add(crossReference);
			}
		}
			
		return crossReferences;
	}

	public List<String> getParents() {
		List<String> parentStrings = new ArrayList<> ();
		
		if (this.base.has("parent")) {
			JSONArray parentJsonArray = this.getJsonArray(this.base, "parent");
			for (int i = 0; i < parentJsonArray.length(); i++) {
				JSONObject parentJsonObject = parentJsonArray.getJSONObject(i);
				String frameId = parentJsonObject.getJSONObject("Reaction").getString("frameid").trim();
				String orgId = parentJsonObject.getJSONObject("Reaction").getString("orgid").trim();
				parentStrings.add(orgId + ":" + frameId);
			}
		}
		
		return parentStrings;
	}
	
	public List<String> getSubInstances() {
		List<String> parentStrings = new ArrayList<> ();
		
		if (this.base.has("reaction-list")) {
//			JSONArray parentJsonArray = this.getJsonArray(this.base, "reaction-list");
			JSONArray jsonArray = this.base.getJSONObject("reaction-list").getJSONArray("Reaction");
//			System.out.println();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				System.out.println(parentJsonObject);
				String frameId = jsonObject.getString("frameid").trim();
				String orgId = jsonObject.getString("orgid").trim();
				parentStrings.add(orgId + ":" + frameId);
			}
		}
		
		return parentStrings;
	}
	
	public List<String> getInstances() {
		List<String> instances = new ArrayList<> ();
		
		if (this.base.has("instance")) {
			JSONArray parentJsonArray = this.getJsonArray(this.base, "instance");
			for (int i = 0; i < parentJsonArray.length(); i++) {
				JSONObject parentJsonObject = parentJsonArray.getJSONObject(i);
				String frameId = parentJsonObject.getJSONObject("Reaction").getString("frameid").trim();
				String orgId = parentJsonObject.getJSONObject("Reaction").getString("orgid").trim();
				instances.add(orgId + ":" + frameId);
			}
		}
		
		return instances;
	}
	
	public List<String> getPathways() {
		List<String> pathwayStrings = new ArrayList<> ();
		
		if (this.base.has("in-pathway")) {
			JSONArray pathwayJsonArray = this.getJsonArray(this.base, "in-pathway");
			
			for (int i = 0; i < pathwayJsonArray.length(); i++) {
				JSONArray pathwayInnerJsonArray = null;
				if (pathwayJsonArray.getJSONObject(i).has("Pathway"))
					pathwayInnerJsonArray = this.getJsonArray(pathwayJsonArray.getJSONObject(i), "Pathway");
				if (pathwayJsonArray.getJSONObject(i).has("Reaction"))
					pathwayInnerJsonArray = this.getJsonArray(pathwayJsonArray.getJSONObject(i), "Reaction");
				
				
				for (int j = 0; j < pathwayInnerJsonArray.length(); j++) {
					String frameId = pathwayInnerJsonArray.getJSONObject(j).getString("frameid").trim();
					String orgId = pathwayInnerJsonArray.getJSONObject(j).getString("orgid").trim();
					pathwayStrings.add(orgId + ":" + frameId);
				}
			}
		}
		
		return pathwayStrings;
	}
	
	public List<String> getEnzymaticReactions() {
		List<String> enzymaticReactionStrings = new ArrayList<> ();
		
		if (this.base.has("enzymatic-reaction")) {
			JSONArray enzymaticReactionJsonArray = this.getJsonArray(this.base, "enzymatic-reaction");
			for (int i = 0; i < enzymaticReactionJsonArray.length(); i++) {
				JSONArray enzymaticReactionInnerJsonArray = this.getJsonArray(
						enzymaticReactionJsonArray.getJSONObject(i), "Enzymatic-Reaction");
				
				for (int j = 0; j < enzymaticReactionInnerJsonArray.length(); j++) {
					String frameId = enzymaticReactionInnerJsonArray.getJSONObject(j).getString("frameid").trim();
					String orgId = enzymaticReactionInnerJsonArray.getJSONObject(j).getString("orgid").trim();
					enzymaticReactionStrings.add(orgId + ":" + frameId);
				}
			}
		}
		
		return enzymaticReactionStrings;
	}
	
	public String getOrphan() {
		if (this.base.has("orphan")) {
			String content = this.base.getString("orphan");
			return content;
			
//			System.out.println("isOrphan WHAT IS " + this.base.get("orphan") + "??????????????");
		}
		
		return null;
	}

	public String getReactionDirection() {
		if (this.base.has("reaction-direction")) {
			String content = this.base.getString("reaction-direction");
			return content;
		}
		return null;
	}

	public Double getGibbs() {
		if (this.base.has("gibbs-0")) {
			JSONObject jsonObject = this.base.getJSONObject("gibbs-0"); 
			Double value = jsonObject.getDouble("content");
			return value;
		}
		return null;
	}
	

}
