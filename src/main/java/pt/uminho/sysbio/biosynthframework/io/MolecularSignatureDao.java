package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.chemanalysis.SignatureSet;

public interface MolecularSignatureDao {

	public SignatureSet getMoleculeMolecularSignature(long cpdId, int h, boolean stereo);
	public void deleteMoleculeMolecularSignature(long cpdId, int h, boolean stereo);
	public void saveMoleculeMolecularSignature(long cpdId, SignatureSet signatureSet);
}
