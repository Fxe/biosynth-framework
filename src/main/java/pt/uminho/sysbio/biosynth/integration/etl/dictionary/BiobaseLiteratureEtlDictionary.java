package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.LiteratureMajorLabel;
import pt.uminho.sysbio.biosynthframework.Metabolite;

public class BiobaseLiteratureEtlDictionary<M extends Metabolite> implements EtlDictionary<String, String> {

	private Map<String, LiteratureMajorLabel> dictionary = new HashMap<> ();
	
	private final Class<M> clazz;
	
	public BiobaseLiteratureEtlDictionary(Class<M> clazz) {
		this.clazz = clazz;
		initVeryHardcodedDictionary(this.clazz);
	}
	
	public void initVeryHardcodedDictionary(Class<M> clazz) {
		//LOL HARDCODED STUFF ! y u no xml config ?
		dictionary.put("patent", LiteratureMajorLabel.Patent);
		dictionary.put("patent accession", LiteratureMajorLabel.Patent);
		
		dictionary.put("citexplore citation", LiteratureMajorLabel.CiteXplore);
		
		dictionary.put("pubmed citation", LiteratureMajorLabel.PubMed);
	}
	
	
	@Override
	public String translate(String lookup) {
		LiteratureMajorLabel label = dictionary.get(lookup.toLowerCase());
		if (label == null) return lookup.toLowerCase();
		return label.toString();
	}

}
