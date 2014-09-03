package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycMetaboliteXMLParser;

public class RestBiocycMetaboliteDaoImpl extends AbstractRestfullBiocycDao 
		implements MetaboliteDao<BioCycMetaboliteEntity> {

	private static Logger LOGGER = Logger.getLogger(RestBiocycMetaboliteDaoImpl.class);
	

//	private final String urlRxnGeneAPI = "http://biocyc.org/apixml?fn=genes-of-reaction&id=META:";
//	private final String urlRxnEnzymeAPI = "http://biocyc.org/apixml?fn=enzymes-of-reaction&id=%s:%s&detail=full";


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
		throw new RuntimeException("Unsupported Operation");
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
		String restCpdQuery = String.format(RestBiocycMetaboliteDaoImpl.xmlGet, pgdb, entry);
		BioCycMetaboliteEntity cpd = null;
		
		
		LOGGER.debug(String.format("Query: %s", restCpdQuery));
		try {
			String localPath = String.format("%scompound/%s/%s.xml", this.getLocalStorage(), pgdb, entry.replaceAll(":", "_"));
			
			String xmlDoc = null;
			
			LOGGER.debug(String.format("Local Path: %s", localPath));
			xmlDoc = this.getLocalOrWeb(restCpdQuery, localPath);
			BioCycMetaboliteXMLParser parser = new BioCycMetaboliteXMLParser(xmlDoc);
			
			cpd = new BioCycMetaboliteEntity();

			String source = parser.getSource();
			String entry_ = parser.getEntry();
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

			cpd.setEntry(entry_);
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
	public List<String> getAllMetaboliteEntries() {
		List<String> cpdEntryList = new ArrayList<>();
		try {
			String params = String.format("[x:x<-%s^^%s]", pgdb, "compounds");
			String restXmlQuery = String.format(xmlquery, URLEncoder.encode(params, "UTF-8"));
			String localPath = this.getLocalStorage() + "query" + "/compound.xml";
			
			String httpResponseString = getLocalOrWeb(restXmlQuery, localPath);
			JSONObject jsDoc = XML.toJSONObject(httpResponseString);
			JSONArray compoundJsArray = jsDoc.getJSONObject("ptools-xml").getJSONArray("Compound");
			for (int i = 0; i < compoundJsArray.length(); i++) {
				String entry = compoundJsArray.getJSONObject(i).getString("frameid");
//				if ( this.entryPrefix.length() > 0) {
//					entry = entryPrefix + ":" + entry;
//				}
				cpdEntryList.add( entry);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("UnsupportedEncodingException [%s]", e.getMessage()));
		} catch (JSONException e) {
			LOGGER.error(String.format("JSONException [%s]", e.getMessage()));
		} catch (IOException e) {
			LOGGER.error(String.format("IOException [%s]", e.getMessage()));
		}
		return cpdEntryList;
	}

}
