package pt.uminho.sysbio.biosynthframework.core.data.io.remote;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericOrganism;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;
import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.IRemoteSource;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggCompoundFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg.KEGGEnzymeFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg.KEGGRPairFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg.KEGGReactionFlatFileParser;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

@Deprecated
public class KeggRemoteSource implements IRemoteSource {
	
	private final static Logger LOGGER = Logger.getLogger(KeggRemoteSource.class.getName());

	public static final boolean VERBOSE = false;
	private static final String SOURCE = "KEGG";
	public static String LOCALCACHE = null;
	public static boolean SAVETOCACHE = false;
	
//	private static final String restList = "http://rest.kegg.jp/list/";
	private static final String restGet = "http://rest.kegg.jp/get/";
	
	private String getLocalOrWeb(String entityType, String entry) throws IOException {
		String entryFlatFile = null;
		
		String baseDirectory = LOCALCACHE.trim().replaceAll("\\\\", "/");
		if ( !baseDirectory.endsWith("/")) baseDirectory = baseDirectory.concat("/");
		String dataFileStr = baseDirectory  + entityType + "/" + entry + ".txt";
		File dataFile = new File(dataFileStr);
		
		System.out.println(dataFile);
		if ( !dataFile.exists()) {
			entryFlatFile =  HttpRequest.get(restGet + String.format("%s:%s", entityType, entry));
			if (SAVETOCACHE) BioSynthUtilsIO.writeToFile(entryFlatFile, dataFileStr);
		} else {
			entryFlatFile = BioSynthUtilsIO.readFromFile(dataFileStr);
		}
		
		return entryFlatFile;
	}
	
	@Override
	public Set<String> getAllReactionIds() {
		Set<String> rxnIds = new HashSet<String>(); 
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "rn")); // keggAPIList("rn");
		String[] compound_array = flat_string.split("\n");
		for ( int i = 0; i < compound_array.length; i++) {
			String[] values = compound_array[i].split("\\t");
			rxnIds.add(values[0].substring(3));
		}
		return rxnIds;
	}
	public Set<String> getAllCompoundIds(boolean includeGlycans) {
		Set<String> cpdIDs = new HashSet<String>();
		String flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "cpd"));
//		if (LOCALCACHE == null) {
//			flat_string = HttpRequest.get(String.format("http://rest.kegg.jp/%s/%s", "list", "cpd")); // keggAPIList("rn");
//		} else {
//			String baseDirectory = LOCALCACHE.trim().replaceAll("\\\\", "/");
//			if ( !baseDirectory.endsWith("/")) baseDirectory = baseDirectory.concat("/");
//			String dataFileStr = baseDirectory  + "query" + "/" + "compoud" + ".txt";
//			flat_string = BioSynthUtilsIO.readFromFile(dataFileStr);
//		}
				
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
		rxn.setOrientation(Orientation.Reversible);
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
	public KeggCompoundMetaboliteEntity getMetaboliteInformation(String cpdId) {
		final String db = cpdId.charAt(0) == 'C' ? "cpd" : "gl"; 
		
		String flatFile = null;
		try {
			if (LOCALCACHE == null) {
				flatFile =  HttpRequest.get(restGet + String.format("%s:%s", db, cpdId));
			} else {
				flatFile = getLocalOrWeb(db, cpdId); //HttpRequest.get( String.format("http://rest.kegg.jp/get/%s:%s", db, cpdId));
			}
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO: " + e.getMessage());
			return null;
		}
		
		if (flatFile == null) {
			LOGGER.log(Level.SEVERE, "Error Retrieve Metabolite - " + cpdId);
			return null;
		}
		
		KeggCompoundFlatFileParser parser = new KeggCompoundFlatFileParser(flatFile);

		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( parser.getEntry());
		cpd.setName( parser.getName());
		cpd.setMass( parser.getMass());
		cpd.setMolWeight( parser.getMolWeight());
		cpd.setFormula( parser.getFormula());
		cpd.setRemark( parser.getRemark());
		cpd.setComment( parser.getComment());
		cpd.setEnzymes( parser.getEnzymes());
		cpd.setReactions( parser.getReactions());
		cpd.setPathways( parser.getPathways());
		cpd.setMetaboliteClass( cpdId.charAt(0) == 'C' ? "COMPOUND" : "GLYCAN");
		cpd.setSource(SOURCE);
		cpd.setCrossReferences( parser.getCrossReferences());

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
