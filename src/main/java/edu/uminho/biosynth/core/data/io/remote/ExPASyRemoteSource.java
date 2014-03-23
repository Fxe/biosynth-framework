package edu.uminho.biosynth.core.data.io.remote;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.data.io.IRemoteSource;
import edu.uminho.biosynth.core.data.io.http.HttpRequest;
import edu.uminho.biosynth.core.data.io.parser.swissprot.ExPASyEnzymeFlatFileParser;
//import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
//import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
//import uk.ac.ebi.kraken.uuw.services.remoting.Query;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;

@Deprecated
public class ExPASyRemoteSource implements IRemoteSource{

	public static boolean VERBOSE = false;
	
	@Deprecated
	public TreeSet<String> getCompoundReactions(String cpdId) {
		return null;
	}

	@Override
	public GenericReaction getReactionInformation(String rxnId) {
		return null;
	}
	@Override
	public GenericMetabolite getMetaboliteInformation(String cpdId) {
		return null;
	}
	@Override
	public GenericEnzyme getEnzymeInformation(String ecId) {
		if (VERBOSE) System.out.println("ExPASyRemoteSource::getEnzymeInformation - " + ecId);
		
		String flatfile = HttpRequest.get("http://enzyme.expasy.org/EC/" + ecId + ".txt");
		if ( flatfile == null) return null;
		ExPASyEnzymeFlatFileParser parser = new ExPASyEnzymeFlatFileParser(flatfile);
		
		GenericEnzyme ecn = null;
		ecn = new GenericEnzyme(parser.getId());
		ecn.setName( parser.getName());
		
//		List<String> entryList = new ArrayList<String> ( parser.getGeneEntrys());
//	    Query query = UniProtQueryBuilder.buildIDListQuery( entryList);
//	    UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
//	    EntryIterator<UniProtEntry> entries = uniProtQueryService.getEntryIterator(query);
//		
//	    for (UniProtEntry entry : entries) {
//	    	ecn.addOrganims(entry.getNcbiTaxonomyIds().get(0).getValue() , entry.getUniProtId().toString());
//	    }
		return ecn;
	}
	@Override
	public GenericReactionPair getPairInformation(String rprId) {
		return null;
	}

	@Override
	public Set<String> getAllReactionIds() {
		Set<String> empty = new HashSet<String> ();
		return empty;
	}
	@Override
	public Set<String> getAllMetabolitesIds() {
		Set<String> empty = new HashSet<String> ();
		return empty;
	}
	@Override
	public Set<String> getAllEnzymeIds() {
		// TODO WORK HERE !
		return null;
	}
	@Override
	public Set<String> getAllReactionPairIds() {
		Set<String> empty = new HashSet<String> ();
		return empty;
	}

	@Override
	public void initialize() {

	}

}
