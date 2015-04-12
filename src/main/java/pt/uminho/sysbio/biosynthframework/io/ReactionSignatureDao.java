package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.chemanalysis.ReactionSignature;

public interface ReactionSignatureDao {

	public ReactionSignature getReactionSignatureById(long rsigId);
	public ReactionSignature getReactionSignature(long rxnId, int h, boolean stereo);
	public boolean deleteReactionSignature(long rxnId, int h, boolean stereo);
	public void saveReactionSignature(long rxnId, ReactionSignature signatureSet);
	public Set<Long> listAllReactionSignatureId(int h, boolean stereo);
	
}
