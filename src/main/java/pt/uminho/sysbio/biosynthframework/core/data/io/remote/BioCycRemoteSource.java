package pt.uminho.sysbio.biosynthframework.core.data.io.remote;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.IRemoteSource;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycEnzymeXMLParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycMetaboliteXMLParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycReactionXMLParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

@Deprecated
public class BioCycRemoteSource implements IRemoteSource {

	private final static Logger LOGGER = Logger.getLogger(BioCycRemoteSource.class.getName());
	
	public static boolean VERBOSE = false;
	public static String LOCALCACHE = null;
	public static boolean SAVETOCACHE = false;
	
	public static String SRC = "BioCyc";
	
	private final String xmlGet = "http://biocyc.org/getxml?";
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
	
	public Set<String> getAllBioCycPGDB() throws IOException {
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
	public BioCycReactionEntity getReactionInformation(String rxnId) {
		
		String pgdb = orgId;
		String xmlDoc = null;
		
		try {
		
			if (rxnId.contains(":")) {
				pgdb = rxnId.replaceFirst(":[^:]+", "");
				xmlDoc = HttpRequest.get(xmlGet + String.format("id=%s", rxnId));
			} else {
				xmlDoc = HttpRequest.get(xmlGet + String.format("id=%s:%s", orgId, rxnId));
			}
		} catch (IOException e) {
			
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

		BioCycReactionEntity rxn = null;
		
		try {
			rxn = new BioCycReactionEntity();
			rxn.setEntry( rxnId);
			rxn.setSource( pgdb);
			rxn.setName( parser.getName());
			rxn.setDescription( parser.getRemark());
			rxn.setOrientation( parser.getOrientation());
//			rxn.setDirection( parser.getOrientationString());
//			rxn.setEquation( parser.getEquation());
//			rxn.setEnzyme( parser.getEnzymes().iterator().next());;
//			EquationParser eqp = new EquationParser( "123456789012345");
//			String[][] left = eqp.getLeftTriplet();
//			String[][] right = eqp.getRightTriplet();
//			rxn.setGeneric( eqp.isVariable());
//			for (String[] l : left) {
//				BioCycReactionLeftEntity leftPair = new BioCycReactionLeftEntity();
//				leftPair.setValue(0.00000000000);
//				leftPair.setCpdEntry("AAAAAAAAA");
//				leftPair.setBioCycReactionEntity(rxn);
//				rxn.getLeft().add(leftPair);
//			}
//			for (String[] r : right) {
//				BioCycReactionRightEntity rightPair = new BioCycReactionRightEntity();
//				rightPair.setValue(0.00000000000);
//				rightPair.setCpdEntry("AAAAAAAAA");
//				rightPair.setBioCycReactionEntity(rxn);
//				rxn.getRight().add(rightPair);
//			}
			rxn.setOrientation( parser.getOrientation());

		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "EQP ERROR " + e.getMessage() + " " + rxnId);
			return null;
		}
		
		return rxn;
	}
	
	private String getLocalOrWeb(String entityType, String biocycDatabase, String entry) throws IOException {
		String entryXml = null;
		
		String baseDirectory = LOCALCACHE.trim().replaceAll("\\\\", "/");
		if ( !baseDirectory.endsWith("/")) baseDirectory = baseDirectory.concat("/");
		String dataFileStr = baseDirectory  + entityType + "/" + biocycDatabase + "/" + entry.replace(':', '_') + ".xml";
		File dataFile = new File(dataFileStr);
		
		System.out.println(dataFile);
		if ( !dataFile.exists()) {
			String arg = String.format("%s:%s", biocycDatabase, entry);
			entryXml = HttpRequest.get(xmlGet + arg);
			if (SAVETOCACHE) BioSynthUtilsIO.writeToFile(entryXml, dataFileStr);
		} else {
			entryXml = BioSynthUtilsIO.readFromFile(dataFileStr);
		}
		
		return entryXml;
	}
	
	@Override
	public BioCycMetaboliteEntity getMetaboliteInformation(String cpdId) {
//		String pgdb = orgId;
//		if ( !cpdId.contains(":")) {
//			pgdb = "META";
//			cpdId = pgdb + ":" + cpdId;
//		}
		
		String xmlDoc = null;
		try {
			if (LOCALCACHE == null) {
				String arg = String.format("%s:%s", orgId, cpdId);
				xmlDoc = HttpRequest.get(xmlGet + arg);
			} else {
				xmlDoc = getLocalOrWeb("compound", orgId, cpdId);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO: " + e.getMessage());
			return null;
		}
		
		if ( xmlDoc == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve - " + cpdId);
			return null;
		}
		
		BioCycMetaboliteXMLParser parser = null;
		
		try {
			parser = new BioCycMetaboliteXMLParser(xmlDoc);
		} catch (JSONException | IOException jsEx) {
			LOGGER.log(Level.SEVERE, "Parse ERROR " + cpdId);
			return null;
		}
		
		if ( !parser.isValid()) {
			LOGGER.log(Level.SEVERE, "Invalid Metabolite XML - " + cpdId);
			return null;
		}
		if (VERBOSE) LOGGER.log(Level.INFO, xmlGet + cpdId);
		BioCycMetaboliteEntity cpd = new BioCycMetaboliteEntity();

		String source = parser.getSource();
		String entry = parser.getEntry();
		String metaboliteClass = parser.getEntityClass();
		String formula = parser.getFormula();
		String name = parser.getName();
		String comment = parser.getComment();
		Integer charge = parser.getCharge();
		Double molWeight = parser.getMolWeight();
		Double cmlMolWeight = parser.getCmlMolWeight();
		String smiles = parser.getSmiles();
		String inchi = parser.getInchi();
		Double gibbs = parser.getGibbs();
		List<BioCycMetaboliteCrossreferenceEntity> crossReferences = parser.getCrossReferences();
		List<String> synonyms = parser.getSynonym();
		List<String> reactions = parser.getReactions();
		List<String> parents = parser.getParents();
		List<String> instances = parser.getInstanses();
		List<String> subclasses = parser.getSubclasses();

		cpd.setEntry(entry);
		cpd.setSource(source);
		cpd.setMetaboliteClass(metaboliteClass);
		cpd.setFormula(formula);
		cpd.setName(name);
		cpd.setComment(comment);
		cpd.setCharge(charge);
		cpd.setMolWeight(molWeight);
		cpd.setCmlMolWeight(cmlMolWeight);
		cpd.setSmiles(smiles);
		cpd.setInChI(inchi);
		cpd.setGibbs(gibbs);
		cpd.setSynonyms(synonyms);
		cpd.setCrossReferences(crossReferences);
		cpd.setReactions(reactions);
		cpd.setParents(parents);
		cpd.setInstances(instances);
		cpd.setSubclasses(subclasses);
		
		return cpd;
	}
	@Override
	public GenericEnzyme getEnzymeInformation(String ecnId) {
		String url = String.format(urlRxnEnzymeAPI, orgId, ecnId);
		String xmlDoc = null; 
		
		try {
			xmlDoc = HttpRequest.get(url);
		} catch (IOException e) {
			
		}
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
		String xmlResponse = null;
		try {
			xmlResponse = HttpRequest.get(xmlquery + String.format("[x:x<-%s^^reactions]", orgId));
		} catch (IOException e) {
			
		}
		return getAllReactionIds(xmlResponse);
	}
	public Set<String> getAllMetabolitesIds(String xmlResponse) {
		try {
			JSONObject jsDoc = XML.toJSONObject(xmlResponse);
			JSONArray compoundJsArray = jsDoc.getJSONObject("ptools-xml").getJSONArray("Compound");
			Set<String> cpdIdSet = new HashSet<String> ();
			for (int i = 0; i < compoundJsArray.length(); i++) {
				String entry = compoundJsArray.getJSONObject(i).getString("frameid");
//				if ( this.entryPrefix.length() > 0) {
//					entry = entryPrefix + ":" + entry;
//				}
				cpdIdSet.add( entry);
			}
			return cpdIdSet;
		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "JSONException");
			return null;
		}
	}
	
	@Override
	public Set<String> getAllMetabolitesIds() {
		String xmlResponse = null;
		
		try {
			if (LOCALCACHE != null) {
				String baseDirectory = LOCALCACHE.trim().replaceAll("\\\\", "/");
				if ( !baseDirectory.endsWith("/")) baseDirectory = baseDirectory.concat("/");
				String queryFileStr = baseDirectory + "query/" + "compound" + ".xml";
				File queryFile = new File(queryFileStr);
				
				if ( !queryFile.exists()) {
					String arg = URLEncoder.encode(String.format("[x:x<-%s^^%ss]", "META", "compound"), "UTF-8");
					xmlResponse = HttpRequest.get(xmlquery + arg);
					BioSynthUtilsIO.writeToFile(xmlResponse, queryFileStr);
				} else {
					xmlResponse = BioSynthUtilsIO.readFromFile(queryFileStr);
				}
			} else {
				String arg = URLEncoder.encode(String.format("[x:x<-%s^^%ss]", "META", "compound"), "UTF-8");
				xmlResponse = HttpRequest.get(xmlquery + arg);
			}
		
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO: " + e.getMessage());
		}
		
		if (xmlResponse == null) return null;
		
		return getAllMetabolitesIds(xmlResponse);
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
