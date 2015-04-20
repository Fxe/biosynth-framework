package pt.uminho.sysbio.biosynthframework.io;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;

public interface MolecularSignatureDao {
	
	public MolecularSignature getMolecularSignatureById(long msigId);
	public MolecularSignature getMolecularSignature(long cpdId, int h, boolean stereo);
	public void deleteMolecularSignature(long cpdId, int h, boolean stereo);
	public void saveMolecularSignature(long cpdId, MolecularSignature signatureSet);
	public Set<Long> getMoleculeReferencesBySignatureSet(MolecularSignature signatureSet);
	public Set<Long> getMoleculeReferencesBySignatureSetId(long signatureSetId);
	
	public Set<Long> findMolecularSignatureContainsAny(Set<Signature> signatures);
	public Set<Long> findMolecularSignatureContainedIn(Map<Signature, Double> signatureMap);
	
}
