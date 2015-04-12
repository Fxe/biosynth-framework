package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;

public interface MolecularSignatureDao {
	
	public MolecularSignature getMolecularSignatureById(long msigId);
	public MolecularSignature getMolecularSignature(long cpdId, int h, boolean stereo);
	public void deleteMolecularSignature(long cpdId, int h, boolean stereo);
	public void saveMolecularSignature(long cpdId, MolecularSignature signatureSet);
}
