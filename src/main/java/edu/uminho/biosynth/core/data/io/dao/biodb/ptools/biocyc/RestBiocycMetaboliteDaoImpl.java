package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycMetaboliteXMLParser;
import edu.uminho.biosynth.core.data.io.http.HttpRequest;

public class RestBiocycMetaboliteDaoImpl extends AbstractRestfullBiocyc {

	private static Logger LOGGER = Logger.getLogger(RestBiocycMetaboliteDaoImpl.class);
	
	private String pgdb = "META";
	
	private final static String xmlGet = "http://biocyc.org/getxml?%s:%s";
	private final String xmlquery = "http://biocyc.org/xmlquery?%s";
//	private final String urlRxnGeneAPI = "http://biocyc.org/apixml?fn=genes-of-reaction&id=META:";
//	private final String urlRxnEnzymeAPI = "http://biocyc.org/apixml?fn=enzymes-of-reaction&id=%s:%s&detail=full";
	
	
	
	public Set<String> getAllBioCycPGDB() {
		try {
			//TODO: apply local caching if enabled
			//String xmlDoc = edu.uminho.biosynth.util.BioSynthUtilsIO.readFromFile("./input/xml/xmlqueryDBS.xml"); 
			String xmlDoc = HttpRequest.get(xmlquery + "dbs");
			if ( xmlDoc == null) {
				LOGGER.error("Error Retrieve - pgdbs");
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
			LOGGER.error(String.format("JSONException - %s", e.getMessage()));
			return null;
		}
	}
	
	public String getPgdb() { return pgdb;}
	public void setPgdb(String pgdb) { this.pgdb = pgdb;}

	@Override
	public BioCycMetaboliteEntity getMetaboliteById(Serializable id) {
		String restCpdQuery = String.format(RestBiocycMetaboliteDaoImpl.xmlGet, pgdb, id);
		BioCycMetaboliteEntity cpd = null;
		
		
		LOGGER.debug(String.format("Query: %s", restCpdQuery));
		try {
			String localPath = String.format("%scompound/%s/%s", this.getLocalStorage(), pgdb, id);
			
			String xmlDoc = null;
			
			LOGGER.debug(String.format("Local Path: %s", localPath));
			xmlDoc = this.getLocalOrWeb(restCpdQuery, localPath);
			BioCycMetaboliteXMLParser parser = new BioCycMetaboliteXMLParser(xmlDoc);
			
			cpd = new BioCycMetaboliteEntity();

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
			List<BioCycMetaboliteCrossReferenceEntity> crossReferences = parser.getCrossReferences();
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
		} catch (IOException e) {
			LOGGER.error(String.format("IO ERROR - %s", e.getMessage()));
		} catch (JSONException e) {
			LOGGER.error(String.format("PARSE ERROR - %s", e.getMessage()));
		}
		
		return cpd;
	}

	@Override
	public BioCycMetaboliteEntity saveMetabolite(
			BioCycMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
//		try {
//			JSONObject jsDoc = XML.toJSONObject(xmlResponse);
//			JSONArray compoundJsArray = jsDoc.getJSONObject("ptools-xml").getJSONArray("Compound");
//			List<Serializable> cpdIdSet = new ArrayList<> ();
//			for (int i = 0; i < compoundJsArray.length(); i++) {
//				String entry = compoundJsArray.getJSONObject(i).getString("frameid");
////				if ( this.entryPrefix.length() > 0) {
////					entry = entryPrefix + ":" + entry;
////				}
//				cpdIdSet.add( entry);
//			}
//			return cpdIdSet;
//		} catch (JSONException ex) {
//			LOGGER.log(Level.SEVERE, "JSONException");
//			
//		}
		
		return null;
	}

	@Override
	public BioCycMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BioCycMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(BioCycMetaboliteEntity entity) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public BioCycMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		// TODO Auto-generated method stub
		return null;
	}

}
