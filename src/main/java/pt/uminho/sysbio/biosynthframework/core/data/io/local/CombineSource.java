package pt.uminho.sysbio.biosynthframework.core.data.io.local;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;
import pt.uminho.sysbio.biosynthframework.core.data.io.ILocalSource;
import pt.uminho.sysbio.biosynthframework.core.data.io.IRemoteSource;

@Deprecated
public class CombineSource implements IRemoteSource, ILocalSource {

	private IRemoteSource remoteSource = null;
	private ILocalSource localSource = null;
	private boolean initialized;
	private boolean cacheMissingData = true;
	
	public CombineSource(IRemoteSource remote, ILocalSource local) {
		this.remoteSource = remote;
		this.localSource = local;
	}

	@Override
	public GenericReaction getReactionInformation(String rxnId) {

		GenericReaction rxn = null;
		
		if ( localSource != null && localSource.hasReactionInformation(rxnId)) {
			rxn = localSource.getReactionInformation(rxnId);
		} else if ( this.remoteSource != null){
			rxn = this.remoteSource.getReactionInformation(rxnId);
			if ( rxn != null && cacheMissingData && localSource != null) {
				localSource.saveReactionInformation(rxn);
			}
		}
		
		return rxn;
	}
	@Override
	public GenericMetabolite getMetaboliteInformation(String cpdId) {
		
		GenericMetabolite cpd = null;

		if ( localSource != null && localSource.hasCompoundInformation(cpdId)) {
			cpd = localSource.getMetaboliteInformation(cpdId);
		} else if ( this.remoteSource != null){
			cpd = this.remoteSource.getMetaboliteInformation(cpdId);
			if ( cpd != null && cacheMissingData && localSource != null) {
				localSource.saveCompoundInformation(cpd);
			}
		}
		
		return cpd;
	}
	@Override
	public GenericEnzyme getEnzymeInformation(String ecnId) {
		
		GenericEnzyme ecn = null;

		if ( localSource != null && localSource.hasEnzymeInformation(ecnId)) {
			ecn = localSource.getEnzymeInformation(ecnId);
		} else if ( this.remoteSource != null){
			ecn = this.remoteSource.getEnzymeInformation(ecnId);
			if ( ecn != null && cacheMissingData && localSource != null) {
				localSource.saveEnzymeInformation(ecn);
			}
		}
		return ecn;
	}
	@Override
	public GenericReactionPair getPairInformation(String rprId) {

		GenericReactionPair rpr = null;

		if ( localSource != null && localSource.hasPairInformation(rprId)) {
			rpr = localSource.getPairInformation(rprId);
		} else if ( this.remoteSource != null){
			rpr = this.remoteSource.getPairInformation(rprId);
			if ( rpr != null && cacheMissingData && localSource != null) {
				localSource.savePairInformation(rpr);
			}
		}
		
		return rpr;
	}

	@Override
	public Set<String> getAllReactionIds() {
		return this.remoteSource.getAllReactionIds();
	}
	@Override
	public Set<String> getAllMetabolitesIds() {
		return this.remoteSource.getAllMetabolitesIds();
	}
	@Override
	public Set<String> getAllEnzymeIds() {
		return this.remoteSource.getAllEnzymeIds();
	}
	@Override
	public Set<String> getAllReactionPairIds() {
		return this.remoteSource.getAllReactionPairIds();
	}

	@Override
	public void initialize() {
		this.localSource.initialize();
		this.remoteSource.initialize();
	}

	@Override
	public boolean hasCompoundInformation(String cpdId) {
		return this.localSource.hasCompoundInformation(cpdId);
	}
	@Override
	public boolean hasReactionInformation(String rxnId) {
		return this.localSource.hasReactionInformation(rxnId);
	}
	@Override
	public boolean hasEnzymeInformation(String ecnId) {
		return this.localSource.hasEnzymeInformation(ecnId);
	}
	@Override
	public boolean hasPairInformation(String rprId) {
		return this.localSource.hasPairInformation(rprId);
	}

	@Override
	public void saveCompoundInformation(GenericMetabolite cpd) {
		this.localSource.saveCompoundInformation(cpd);
	}
	@Override
	public void saveReactionInformation(GenericReaction rxn) {
		this.localSource.saveReactionInformation(rxn);
	}
	@Override
	public void saveEnzymeInformation(GenericEnzyme ecn) {
		this.localSource.saveEnzymeInformation(ecn);
	}
	@Override
	public void savePairInformation(GenericReactionPair rpr) {
		this.localSource.savePairInformation(rpr);
	}
	
	public IRemoteSource getRemoteSource() {
		return remoteSource;
	}

	public void setRemoteSource(IRemoteSource remoteSource) {
		this.remoteSource = remoteSource;
	}

	public ILocalSource getLocalSource() {
		return localSource;
	}

	public void setLocalSource(ILocalSource localSource) {
		this.localSource = localSource;
	}
	


	public void setCacheMissingData(boolean cacheMissingData) {
		this.cacheMissingData = cacheMissingData;
	}
	

	@Override
	public boolean removeReactionInformation(String rxnID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addMetatagToReaction(String rxnId, String metatag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMetatagsToReaction(String rxnId, Set<String> metatags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMetatagsToReactions(Map<String, Set<String>> metatags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

}
