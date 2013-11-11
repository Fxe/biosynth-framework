package edu.uminho.biosynth.core.data.io.remote;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericOrganism;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.data.io.IRemoteSource;
import edu.uminho.biosynth.core.data.io.http.HttpRequest;
import edu.uminho.biosynth.core.data.io.parser.kegg.KEGGCompoundFlatFileParser;
import edu.uminho.biosynth.core.data.io.parser.kegg.KEGGEnzymeFlatFileParser;
import edu.uminho.biosynth.core.data.io.parser.kegg.KEGGRPairFlatFileParser;
import edu.uminho.biosynth.core.data.io.parser.kegg.KEGGReactionFlatFileParser;
import edu.uminho.biosynth.util.EquationParser;

public class KeggRemoteSource implements IRemoteSource {
	
	private final static Logger LOGGER = Logger.getLogger(KeggRemoteSource.class.getName());

	public static boolean VERBOSE = false;
	private static final String SOURCE = "KEGG";
	
//	@Deprecated
//	private String keggAPIList(String list) {
//		String ret = null;
//		
////		try {
////			ret = KeggRestful.fetch( KEGGOPERATION.LIST, list);
////		} catch (URIException ex) { }
//		
//		return ret;
//	}
	
//	@Deprecated
//	private String keggAPILink(String db, String orgId) {
//		String ret = null;
//		
////		try {
////			ret = KeggRestful.fetch( KEGGOPERATION.LINK, db, orgId);
////		} catch (URIException ex) { }
//		
//		return ret;
//	}
	
//	@Deprecated
//	public TreeSet<String> getCompoundReactions(String cpID) {
//		
//		TreeSet<String> retList = null;
////		TreeSet<String> reactions = null;
////		try {
////			reactions = KEGGAPI.get_reactions_by_compounds( cpID);
////		} catch (Exception e) {
////			System.err.println("Error::" + e.getMessage());
////		}
////		
////		
////		if (reactions != null) {
////			List<String> reactions_list = new ArrayList<String>( reactions);
////			for (int i = 0; i < reactions_list.size(); i++) {
////				String reaction_string = reactions_list.get( i);
////				String[] reaction = reaction_string.split(":");
////				reactions_list.set( i, reaction[1]);
////			}
////		
////			retList = new TreeSet<String>( reactions_list);
////		}
////		
//		
//		return retList;
//	}
	@Override
	public Set<String> getAllReactionIds() {
		Set<String> rxnIDs = new HashSet<String>(); 
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "rn")); // keggAPIList("rn");
		String[] compound_array = flat_string.split("\n");
		for ( int i = 0; i < compound_array.length; i++) {
			String[] values = compound_array[i].split("\\t");
			rxnIDs.add(values[0].substring(3));
		}
		return rxnIDs;
	}
	public Set<String> getAllCompoundIds(boolean includeGlycans) {
		Set<String> cpdIDs = new HashSet<String>(); 
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "cpd")); // keggAPIList("rn");
		String[] compound_array = flat_string.split("\n");
		for ( int i = 0; i < compound_array.length; i++) {
			String[] values = compound_array[i].split("\\t");
			cpdIDs.add(values[0].substring(4));
		}
if (VERBOSE) System.out.println( "#CPD:" + cpdIDs.size());
		if ( includeGlycans) {
			flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "gl"));
			compound_array = flat_string.split("\n");
if (VERBOSE) System.out.println( "#GLY:" + compound_array.length);
			for ( int i = 0; i < compound_array.length; i++) {
				String[] values = compound_array[i].split("\\t");
				cpdIDs.add(values[0].substring(3));
			}
		}

		return cpdIDs;
	}
	@Override
	public Set<String> getAllMetabolitesIds() {
		return getAllCompoundIds( true);
	}
	@Override
	public Set<String> getAllEnzymeIds() {
		Set<String> ecnIdSet = new HashSet<String>(); 
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "ec"));
		//String flat_string = keggAPIList("ec");
		String[] compound_array = flat_string.split("\n");
		for ( int i = 0; i < compound_array.length; i++) {
			String[] values = compound_array[i].split("\\t");
			ecnIdSet.add(values[0].substring(3));
		}
		
		return ecnIdSet;
	}
	@Override
	public Set<String> getAllReactionPairIds() {
		Set<String> rprIdSet = new HashSet<String>();
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "rp"));
		//String flat_string = keggAPIList("rp");
		String[] compound_array = flat_string.split("\n");
		for ( int i = 0; i < compound_array.length; i++) {
			String[] values = compound_array[i].split("\\t");
			rprIdSet.add(values[0].substring(3));
		}
		return rprIdSet;
	}
	
	public Set<String> getOrganimsReactionIdSet(String orgId) {
		Set<String> rxnIdSet = new HashSet<String> ();
		String retVal = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s/%s", "link", "rn", orgId)); //keggAPILink("rn", orgId);
		Pattern reactionPattern = Pattern.compile("rn:(R[0-9]+)");
		Matcher parser = reactionPattern.matcher( retVal);
		while ( parser.find()) {
			rxnIdSet.add(parser.group(1));
		}
		//System.out.println(rxnIdSet);
		return rxnIdSet;
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public KeggReactionEntity getReactionInformation(String rxnId) {
		String flatFile = HttpRequest.get(String.format("http://rest.kegg.jp/get/%s:%s", "rn", rxnId));
		if (flatFile == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve Reaction - " + rxnId);
			return null;
		}
		
		KEGGReactionFlatFileParser parser = new KEGGReactionFlatFileParser(flatFile);
		KeggReactionEntity rxn = new KeggReactionEntity();
		rxn.setEntry(rxnId);
		rxn.setComment(null);
		rxn.setDefinition(null);
		rxn.setRemark(parser.getRemark());
		rxn.setOrientation(0);
		rxn.setName(parser.getName());
		
//		EquationParser eqp = new EquationParser( rxn.getEquation());
				
////		KeggReactionInformation rInfo = null;
//		
////		try {
////			rInfo = KEGGAPI.get_reaction_by_keggId( rxnId);
////		} catch (Exception e) {
////			LOGGER.log(Level.SEVERE, "Error Retrieve - " + rxnId + " " + e.getMessage());
////			return null;
////		}
//		
//		KeggReactionEntity rxn = new KeggReactionEntity();
//		rxn.setId(rxnId);
////		rxn.addEnzymes( parser.getEnzymes());
////		if ( parser.getSimilarReactions() != null) {
////			rxn.addSameAs( parser.getSimilarReactions());
////		}
//		// ALL KEGG Reactions are <=>
//		rxn.setOrientation(0);
//		
//		rxn.setName( parser.getName());
//		
//		rxn.setEquation( parser.getEquation());
////		rxn.addSameAs( parser.getSimilarReactions());
////		rxn.addRPairs( parser.getRPair());
//		rxn.setDescription( parser.getRemark());
//		rxn.setSource(SOURCE);
//		
////		EquationParser eqp = new EquationParser( rxn.getEquation());
////		String[][] left = eqp.getLeftTriplet();
////		String[][] right = eqp.getRightTriplet();
////		rxn.setGeneric( eqp.isVariable());
////		rxn.addReactants(left);
////		rxn.addProducts(right);
		
		return rxn;
	}
	@Override
	public KeggMetaboliteEntity getMetaboliteInformation(String cpdId) {
		final String db = cpdId.charAt(0) == 'C' ? "cpd" : "gl"; 
		String flatFile = HttpRequest.get( String.format("http://rest.kegg.jp/get/%s:%s", db, cpdId));
		
		if (flatFile == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve Metabolite - " + cpdId);
			return null;
		}
		
		KEGGCompoundFlatFileParser parser = new KEGGCompoundFlatFileParser(flatFile);

		KeggMetaboliteEntity cpd = new KeggMetaboliteEntity();
		cpd.setEntry(cpdId);
		cpd.setName( parser.getName());
		cpd.setFormula( parser.getFormula());
//		cpd.addReactions( parser.getReactions());
		cpd.setMetaboliteClass( parser.getMetaboliteClass());
		cpd.setSource(SOURCE);

		return cpd;
	}
	@Override
	public GenericEnzyme getEnzymeInformation(String ecnId) {
		String flatfile = HttpRequest.get(String.format("http://rest.kegg.jp/get/%s:%s", "ec", ecnId));
		
		if (flatfile == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve Enzyme - " + ecnId);
			return null;
		}
		
		KEGGEnzymeFlatFileParser parser = new KEGGEnzymeFlatFileParser( flatfile);

		GenericEnzyme ecn = null;
		ecn = new GenericEnzyme(ecnId);
		ecn.setName( parser.getName());
		ecn.addOrganimsMap( parser.getOrganisms());
		ecn.setSource(SOURCE);
		
		return ecn;
	}
	@Override
	public GenericReactionPair getPairInformation(String rprId) {		
		String flatFile = HttpRequest.get( String.format("http://rest.kegg.jp/get/%s:%s", "rp", rprId));
		
		if (flatFile == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve Pair - " + rprId);
			return null;
		}
		
		KEGGRPairFlatFileParser parser = new KEGGRPairFlatFileParser( flatFile);
		
		GenericReactionPair rpr = null;
		rpr = new GenericReactionPair( rprId, parser.getName(), parser.getType());
		rpr.addReactions( parser.getReactions());
		rpr.addRelatedPairs( parser.getRelatedPairs());
		GenericMetabolite entry1 = new GenericMetabolite( parser.getEntry1());
		GenericMetabolite entry2 = new GenericMetabolite( parser.getEntry2());
		rpr.setEntry1( entry1);
		rpr.setEntry2( entry2);
		rpr.setSource(SOURCE);
		
		return rpr;
	}
	//@Override
	public GenericOrganism getOrganismInformation(String orgId) {
		
		return null;
	}


}
