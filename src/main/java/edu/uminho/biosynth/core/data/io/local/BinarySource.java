package edu.uminho.biosynth.core.data.io.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.data.io.ILocalSource;

public class BinarySource implements ILocalSource {

	public static boolean VERBOSE = false;

	private HashMap<String, GenericReaction>		storedReactions_;
	private HashMap<String, GenericMetabolite>		storedCompounds_;
	private HashMap<String, GenericEnzyme>			storedEnzymes_;
	private HashMap<String, GenericReactionPair>	storedReactionPair_;
	private boolean loaded_ = false;
	private String datapath_;
	
	public BinarySource(String datapath) {
		this.datapath_ = datapath;
	}

	@Override
	public GenericReaction getReactionInformation(String rcID) {
		return storedReactions_.get(rcID);
	}

	@Override
	public GenericMetabolite getMetaboliteInformation(String cpID) {
		return storedCompounds_.get(cpID);
	}

	@Override
	public GenericEnzyme getEnzymeInformation(String ecID) {
		return storedEnzymes_.get(ecID);
	}

	@Override
	public Set<String> getAllReactionIds() {
		return this.storedReactions_.keySet();
	}

	@Override
	public Set<String> getAllMetabolitesIds() {
		return this.storedCompounds_.keySet();
	}

	@Override
	public Set<String> getAllEnzymeIds() {
		return this.storedEnzymes_.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize() {
		
		File os = new File(datapath_);
		if ( os.exists()) {
if ( VERBOSE) System.out.println( "File (" + datapath_ + ") Found Loading Data");
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream( new FileInputStream( os));
				this.storedCompounds_ = (HashMap<String, GenericMetabolite>) ois.readObject();
				this.storedReactions_ = (HashMap<String, GenericReaction>) ois.readObject();
				this.storedEnzymes_   = (HashMap<String, GenericEnzyme>)   ois.readObject();
				this.storedReactionPair_ = (HashMap<String, GenericReactionPair>) ois.readObject();
			} catch ( FileNotFoundException fnfEx) {
				System.err.println( "BinarySource::initialize - FILENOTFOUND " + fnfEx.getMessage());
			} catch ( ClassNotFoundException cnfEx) {
				System.err.println( "BinarySource::initialize - CLASSNOTFOUND " + cnfEx.getMessage());
			} catch ( IOException ioEx) {
				System.err.println( "BinarySource::initialize - IOEX " + ioEx.getMessage());
			} finally {
				try {
					if ( ois != null) ois.close();
				} catch ( IOException ioEx) {
					
				}
			}
		} else {
if ( VERBOSE) System.out.println( "File (" + datapath_ + ") Not Found - Empty File Created");
			this.storedCompounds_ = new HashMap<String, GenericMetabolite>();
			this.storedReactions_ = new HashMap<String, GenericReaction>();
			this.storedEnzymes_   = new HashMap<String, GenericEnzyme>();
			this.storedReactionPair_ = new HashMap<String, GenericReactionPair>();
		}
		
		this.loaded_ = true;
	}
	
	public void save() {
		this.save( this.datapath_);
	}
	
	public void save(String filepath) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream( new FileOutputStream(filepath));
			oos.writeObject( this.storedCompounds_);
			oos.writeObject( this.storedReactions_);
			oos.writeObject( this.storedEnzymes_  );
			oos.writeObject( this.storedReactionPair_);
		} catch ( FileNotFoundException fnfEx) {
			
		} catch ( IOException ioEx) {
			
		} finally {
			try {
			   if ( oos != null) {
				   oos.close();
			   }
			} catch ( IOException ioEx) {
				
			} 
		}
	}

	@Override
	public boolean hasCompoundInformation(String cpID) {
		return this.storedCompounds_.containsKey(cpID);
	}

	@Override
	public boolean hasReactionInformation(String rcID) {
		return this.storedReactions_.containsKey(rcID);
	}

	@Override
	public boolean hasEnzymeInformation(String ecID) {
		return this.storedEnzymes_.containsKey(ecID);
	}

	@Override
	public void saveCompoundInformation(GenericMetabolite cInfo) {
		this.storedCompounds_.put( cInfo.getEntry(), cInfo);
	}

	@Override
	public void saveReactionInformation(GenericReaction rInfo) {
		this.storedReactions_.put( rInfo.getEntry(), rInfo);
	}

	@Override
	public void saveEnzymeInformation(GenericEnzyme eInfo) {
		this.storedEnzymes_.put( eInfo.getEntry(), eInfo);
	}

	@Override
	public boolean isInitialized() {
		return this.loaded_;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("File\t").append( this.datapath_).append('\n');
		sb.append("#cpd\t").append( this.storedCompounds_.size()).append('\n');
		sb.append("#rxn\t").append( this.storedReactions_.size()).append('\n');
		sb.append("#ecn\t").append( this.storedEnzymes_  .size()).append('\n');
		sb.append("#rpr\t").append( this.storedReactionPair_.size());
		return sb.toString();
	}

	@Override
	public GenericReactionPair getPairInformation(String rprID) {
		return this.storedReactionPair_.get(rprID);
	}

	@Override
	public Set<String> getAllReactionPairIds() {
		return this.storedReactionPair_.keySet();
	}

	@Override
	public void savePairInformation(GenericReactionPair rpInfo) {
		this.storedReactionPair_.put(rpInfo.getEntry(), rpInfo);
	}

	@Override
	public boolean hasPairInformation(String rprID) {
		return this.storedReactionPair_.containsKey(rprID);
	}

	@Override
	public boolean removeReactionInformation(String rxnID) {
		if ( this.storedReactions_.remove(rxnID) == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public void addMetatagToReaction(String rxnId, String metatag) {
//		this.storedReactions_.get(rxnId).addMetatag(metatag);
	}

	@Override
	public void addMetatagsToReaction(String rxnId, Set<String> metatags) {
//		this.storedReactions_.get(rxnId).addMetatags(metatags);
	}

	@Override
	public void addMetatagsToReactions(Map<String, Set<String>> metatags) {
		for ( String rxnId : metatags.keySet()) {
//			this.storedReactions_.get(rxnId).addMetatags( metatags.get(rxnId));
		}
	}
	
	
}
