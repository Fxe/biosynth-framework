package edu.uminho.biosynth.core.data.io.remote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.data.io.IRemoteSource;
import edu.uminho.biosynth.core.data.io.http.HttpRequest;
import edu.uminho.biosynth.core.data.io.parser.biocyc.BioCycEnzymeXMLParser;
import edu.uminho.biosynth.core.data.io.parser.biocyc.BioCycMetaboliteXMLParser;
import edu.uminho.biosynth.core.data.io.parser.biocyc.BioCycReactionXMLParser;
import edu.uminho.biosynth.util.EquationParser;

public class BioCycRemoteSource implements IRemoteSource {

	private final static Logger LOGGER = Logger.getLogger(BioCycRemoteSource.class.getName());
	
	public static boolean VERBOSE = false;
	
	public static String SRC = "BioCyc";
	
	private final String url = "http://biocyc.org/getxml?";
	private final String xmlquery = "http://biocyc.org/xmlquery?";
//	private final String urlRxnGeneAPI = "http://biocyc.org/apixml?fn=genes-of-reaction&id=META:";
	private final String urlRxnEnzymeAPI = "http://biocyc.org/apixml?fn=enzymes-of-reaction&id=%s:%s&detail=full";
	
	private String orgId;
	private final String entryPrefix;
	
	public BioCycRemoteSource(String ordId) {
		this.orgId = ordId;
		this.entryPrefix = ordId;
	}
	
	public BioCycRemoteSource(String ordId, String prefix) {
		this.orgId = ordId;
		this.entryPrefix = prefix;
	}
	
	public Set<String> getAllBioCycPGDB() {
		try {
			//String xmlDoc = edu.uminho.biosynth.util.BioSynthUtilsIO.readFromFile("./input/xml/xmlqueryDBS.xml"); 
			String xmlDoc = HttpRequest.get(xmlquery + "dbs");
			if ( xmlDoc == null) {
				LOGGER.log(Level.SEVERE, "Error Retrieve - pgdbs");
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
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}

	@Override
	public GenericReaction getReactionInformation(String rxnId) {
		String pgdb = orgId;
		String xmlDoc;
		if (rxnId.contains(":")) {
			pgdb = rxnId.replaceFirst(":[^:]+", "");
			xmlDoc = HttpRequest.get(url + String.format("id=%s", rxnId));
		} else {
			xmlDoc = HttpRequest.get(url + String.format("id=%s:%s", orgId, rxnId));
		}
		
		if ( xmlDoc == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve - " + rxnId);
			return null;
		}
		
		BioCycReactionXMLParser parser;
		try {
			parser = new BioCycReactionXMLParser(xmlDoc);
			if ( !parser.isValid()) {
				LOGGER.log(Level.SEVERE, "Invalid Reaction XML - " + rxnId);
				return null;
			}
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException - " + rxnId);
			return null;
		}
		
		String entry = parser.getEntry();
		if (rxnId.contains(":")) {
			entry = rxnId;
		} else if ( this.entryPrefix.length() > 0) {
			entry = entryPrefix + ":" + entry;
		}

		GenericReaction rxn = null;
		
		try {
			rxn = new GenericReaction( entry);
			rxn.setSource( pgdb);
			rxn.setName( parser.getName());
			rxn.setDescription( parser.getRemark());
			rxn.setEquation( parser.getEquation());
			rxn.addEnzymes( parser.getEnzymes());;
			EquationParser eqp = new EquationParser( rxn.getEquation());
			String[][] left = eqp.getLeftTriplet();
			String[][] right = eqp.getRightTriplet();
			rxn.setGeneric( eqp.isVariable());
			rxn.addReactants(left);
			rxn.addProducts(right);
			rxn.setOrientation( parser.getOrientation());

		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "EQP ERROR " + e.getMessage() + " " + rxnId);
			return null;
		}
		
		return rxn;
	}
	@Override
	public GenericMetabolite getMetaboliteInformation(String cpdId) {
		String pgdb = orgId;
		if ( !cpdId.contains(":")) {
			pgdb = "META";
			cpdId = pgdb + ":" + cpdId;
		}
		
		String xmlDoc = HttpRequest.get(url + cpdId);
		if ( xmlDoc == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve - " + cpdId);
			return null;
		}
		
		BioCycMetaboliteXMLParser parser = null;
		
		try {
			parser = new BioCycMetaboliteXMLParser(xmlDoc);
		} catch (JSONException jsEx) {
			LOGGER.log(Level.SEVERE, "Parse ERROR " + cpdId);
			return null;
		}
		
		if ( !parser.isValid()) {
			LOGGER.log(Level.SEVERE, "Invalid Metabolite XML - " + cpdId);
			return null;
		}
		if (VERBOSE) LOGGER.log(Level.INFO, url + cpdId);
		GenericMetabolite cpd = null;
		
//			String entry = parser.getEntry();
//			if ( this.entryPrefix.length() > 0) {
//				entry = entryPrefix + ":" + entry;
//			}
		cpd = new GenericMetabolite( parser.getEntry());
		cpd.setSource( pgdb);
		cpd.setName( parser.getName());
		cpd.setMetaboliteClass( parser.getEntityClass());
		Set<String> rxnIdSet = new HashSet<> ();
		for (String rxnId : parser.getReactions())
			rxnIdSet.add(this.orgId.concat(":").concat(rxnId));
		cpd.setReactionIdSet( rxnIdSet);
		cpd.setFormula( parser.getFormula());
		cpd.setDescription( parser.getRemark());
		
		return cpd;
	}
	@Override
	public GenericEnzyme getEnzymeInformation(String ecnId) {
		String url = String.format(urlRxnEnzymeAPI, orgId, ecnId);
		String xmlDoc = HttpRequest.get(url);
		if ( xmlDoc == null) {
			if (VERBOSE) LOGGER.log(Level.SEVERE, "Error Retrieve - " + ecnId);
			return null;
		}
		
		BioCycEnzymeXMLParser parser = null;
		try {
			parser = new BioCycEnzymeXMLParser(xmlDoc);
		} catch (JSONException jsEx) {
			LOGGER.log(Level.SEVERE, "Parsing Error Enzyme XML - " + ecnId);
			return null;
		}
		if ( !parser.isValid()) {
			LOGGER.log(Level.SEVERE, "Invalid Enzyme XML - " + ecnId);
			return null;
		}
		
//		if ( !parser.isValid()) {
//			if (VERBOSE) LOGGER.log(Level.SEVERE, "Invalid Enzyme XML - " + cpdId);
//			return null;
//		}
		
		GenericEnzyme ecn = null;
		try {
			String entry = ecnId;
			if ( this.entryPrefix.length() > 0) {
				entry = entryPrefix + ":" + entry;
			}
			ecn = new GenericEnzyme( entry);
			ecn.setName("");
			ecn.setSource( orgId);
			Map<String, String> orgMap = new HashMap<String, String> ();
			for (int i = 0; i < parser.numberOfGenes(); i++) {
				Map<String, String> proteinMap = parser.getOrganism(i);
				for (String k : proteinMap.keySet()) {
					if ( orgMap.containsKey(k)) {
						orgMap.put(k, orgMap.get(k) + " " +  proteinMap.get(k));
					} else {
						orgMap.put(k, proteinMap.get(k));
					}
				}
//				System.out.println( orgMap);
//				ecn.addOrganimsMap(orgMap);
//				ecn.addOrganims("lol", parser.getEntry(i));
			}
			ecn.addOrganimsMap(orgMap);
		} catch (JSONException jsEx) {
			ecn = null;
		}
		
		return ecn;
	}
	@Override
	public GenericReactionPair getPairInformation(String rprId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAllReactionIds(String xmlResponse) {
		try {
			JSONObject jsDoc = XML.toJSONObject(xmlResponse);
			JSONArray reactions = jsDoc.getJSONObject("ptools-xml").getJSONArray("Reaction");
			Set<String> rxnIdSet = new HashSet<String> ();
			for (int i = 0; i < reactions.length(); i++) {
				String entry = reactions.getJSONObject(i).getString("frameid");
				if ( this.entryPrefix.length() > 0) {
					entry = entryPrefix + ":" + entry;
				}
				rxnIdSet.add( entry);
			}
			return rxnIdSet;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	@Override
	public Set<String> getAllReactionIds() {
		String xmlResponse = HttpRequest.get(xmlquery + String.format("[x:x<-%s^^reactions]", orgId));
		return getAllReactionIds(xmlResponse);
	}
	public Set<String> getAllMetabolitesIds(String xmlResponse) {
		try {
			JSONObject jsDoc = XML.toJSONObject(xmlResponse);
			JSONArray reactions = jsDoc.getJSONObject("ptools-xml").getJSONArray("Compound");
			Set<String> rxnIdSet = new HashSet<String> ();
			for (int i = 0; i < reactions.length(); i++) {
				String entry = reactions.getJSONObject(i).getString("frameid");
				if ( this.entryPrefix.length() > 0) {
					entry = entryPrefix + ":" + entry;
				}
				rxnIdSet.add( entry);
			}
			return rxnIdSet;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	@Override
	public Set<String> getAllMetabolitesIds() {
		//String xmlResponse = HttpRequest.get(xmlquery + String.format("[x:x<-%s^^compounds]", orgId));
		return null;
	}
	@Override
	public Set<String> getAllEnzymeIds() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getAllReactionPairIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
