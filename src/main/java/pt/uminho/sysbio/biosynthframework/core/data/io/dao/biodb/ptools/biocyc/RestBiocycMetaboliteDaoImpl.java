package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycMetaboliteXMLParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class RestBiocycMetaboliteDaoImpl extends AbstractRestfullBiocycDao 
		implements MetaboliteDao<BioCycMetaboliteEntity> {

	private static Logger LOGGER = LoggerFactory.getLogger(RestBiocycMetaboliteDaoImpl.class);
	

//	private final String urlRxnGeneAPI = "http://biocyc.org/apixml?fn=genes-of-reaction&id=META:";
//	private final String urlRxnEnzymeAPI = "http://biocyc.org/apixml?fn=enzymes-of-reaction&id=%s:%s&detail=full";

	public void createFolderIfNotExists(String path) {
		File file = new File(path);
		
		if (!file.exists()) {
			LOGGER.info(String.format("Make Dir %s", path));
			file.mkdirs();
		}
	}

	@Override
	public BioCycMetaboliteEntity getMetaboliteById(Serializable id) {
		throw new RuntimeException("Unsupported Operation.");
//		String restCpdQuery = String.format(RestBiocycMetaboliteDaoImpl.xmlGet, pgdb, id);
//		BioCycMetaboliteEntity cpd = null;
//		
//		LOGGER.debug(String.format("Query: %s", restCpdQuery));
//		try {
//			String localPath = String.format("%s/%s/compound/", this.getLocalStorage(), pgdb, id);
//			createFolderIfNotExists(localPath);
//			String xmlDoc = null;
//			
//			LOGGER.debug(String.format("Local Path: %s", localPath));
//			xmlDoc = this.getLocalOrWeb(restCpdQuery, localPath);
//			BioCycMetaboliteXMLParser parser = new BioCycMetaboliteXMLParser(xmlDoc);
//			
//			cpd = new BioCycMetaboliteEntity();
//
//			String frameId = parser.getFrameId();
//			String source = parser.getSource();
//			String entry = parser.getEntry();
//			String metaboliteClass = parser.getEntityClass();
//			String formula = parser.getFormula();
//			String name = parser.getName();
//			String comment = parser.getComment();
//			Integer charge = parser.getCharge();
//			Double molWeight = parser.getMolWeight();
//			Double cmlMolWeight = parser.getCmlMolWeight();
//			String smiles = parser.getSmiles();
//			String inchi = parser.getInchi();
//			Double gibbs = parser.getGibbs();
//			List<BioCycMetaboliteCrossreferenceEntity> crossReferences = parser.getCrossReferences();
//			List<String> synonyms = parser.getSynonym();
//			List<String> reactions = parser.getReactions();
//			List<String> parents = parser.getParents();
//			List<String> instances = parser.getInstanses();
//			List<String> subclasses = parser.getSubclasses();
//
//			cpd.setFrameId(frameId);
//			cpd.setEntry(entry);
//			cpd.setSource(source);
//			cpd.setMetaboliteClass(metaboliteClass);
//			cpd.setFormula(formula);
//			cpd.setName(name);
//			cpd.setComment(comment);
//			cpd.setCharge(charge);
//			cpd.setMolWeight(molWeight);
//			cpd.setCmlMolWeight(cmlMolWeight);
//			cpd.setSmiles(smiles);
//			cpd.setInChI(inchi);
//			cpd.setGibbs(gibbs);
//			cpd.setSynonyms(synonyms);
//			cpd.setCrossReferences(crossReferences);
//			cpd.setReactions(reactions);
//			cpd.setParents(parents);
//			cpd.setInstances(instances);
//			cpd.setSubclasses(subclasses);
//		} catch (IOException e) {
//			LOGGER.error(String.format("IO ERROR - %s", e.getMessage()));
//		} catch (JSONException e) {
//			LOGGER.error(String.format("PARSE ERROR - %s", e.getMessage()));
//		}
		
//		return cpd;
	}

	@Override
	public BioCycMetaboliteEntity saveMetabolite(
			BioCycMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		throw new RuntimeException("Unsupported Operation");
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
			String localPath = String.format("%s/%s/compound/", this.getLocalStorage(), pgdb);
			createFolderIfNotExists(localPath);
			localPath = localPath.concat(String.format("%s.xml", entry.replaceAll(":", "_")));
			String xmlDoc = null;
			
			LOGGER.debug(String.format("Local Path: %s", localPath));
			xmlDoc = this.getLocalOrWeb(restCpdQuery, localPath);
			BioCycMetaboliteXMLParser parser = new BioCycMetaboliteXMLParser(xmlDoc);
			
			if (!parser.isValid()) return null;
			
			cpd = new BioCycMetaboliteEntity();

			String frameId = parser.getFrameId();
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

			cpd.setFrameId(frameId);
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
			cpd = null;
		} catch (JSONException e) {
			LOGGER.error(String.format("PARSE ERROR - %s", e.getMessage()));
			cpd = null;
		}
		
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> cpdEntryList = new ArrayList<>();
		try {
			String params = String.format("[x:x<-%s^^%s]", pgdb, "compounds");
			String restXmlQuery = String.format(xmlquery, URLEncoder.encode(params, "UTF-8"));
			String localPath = String.format("%s/%s/query/", this.getLocalStorage(), pgdb);
			createFolderIfNotExists(localPath);
			localPath = localPath.concat("compound.xml");
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
