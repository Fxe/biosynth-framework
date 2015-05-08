package pt.uminho.sysbio.biosynthframework.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;
import pt.uminho.sysbio.biosynthframework.util.DigestUtils;

public class FaulonXtmsMolecularSignatureDaoImpl implements MolecularSignatureDao {
	
	private Map<Long, Signature> signatureMap = new HashMap<> ();
	private Map<Long, MolecularSignature> msigMap = new HashMap<> ();
	private Map<String, Long> entryToId = new HashMap<> ();
	private Map<Long, String> idToEntry = new HashMap<> ();
	private Map<Signature, Set<Long>> signatureToMsig = new HashMap<> ();
	private Map<String, Set<Long>> hash64ToMsigId = new HashMap<> ();
	private Map<Long, String> msigIdToHash64 = new HashMap<> ();
	
	public FaulonXtmsMolecularSignatureDaoImpl(String sp, String cpmd, int h, boolean stereo) {
		try {
			long i = 0;
			for (String signature : IOUtils.readLines(new FileInputStream(sp))){
				signatureMap.put(i++, new Signature(signature));
			}
 
			i = 0;
			for (String line : IOUtils.readLines(new FileInputStream(cpmd))){
				String[] cols = line.trim().split(" ");
				
				Map<Signature, Double> signatureMap = parseSignatureMap(cols[1].trim());
				
				MolecularSignature msig = new MolecularSignature();
				msig.setH(h);
				msig.setStereo(stereo);
				msig.setSignatureMap(signatureMap);
				
				for (Signature sig : signatureMap.keySet()) {
					if (!signatureToMsig.containsKey(sig)) {
						signatureToMsig.put(sig, new HashSet<Long> ());
					}
					signatureToMsig.get(sig).add(i);
				}
				
				String hash64 = DigestUtils.hex(msig.hash());
				if (!hash64ToMsigId.containsKey(hash64)) {
					hash64ToMsigId.put(hash64, new HashSet<Long> ());
				}
				hash64ToMsigId.get(hash64).add(i);
				msigIdToHash64.put(i, hash64);
				
				msigMap.put(i, msig);
				entryToId.put(cols[0].trim(), i);
				idToEntry.put(i, cols[0].trim());
				i++;
			}
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * Example: 1.0:0,1.0:1,1.0:2,2.0:3,2.0:4,1.0:5,1.0:6,1.0:7,1.0:8,1.0:9,1.0:10,1.0:11,1.0:12,1.0:13,1.0:14,1.0:15,1.0:16
	 * @param str
	 * @return
	 */
	public Map<Signature, Double> parseSignatureMap(String str) {
		Map<Signature, Double> signatureMap = new HashMap<> ();
		for (String sig : str.split(",")) {
			String[] cols = sig.split(":");
			double value = Double.parseDouble(cols[0].trim());
			long sigId = Long.parseLong(cols[1].trim());
			Signature signature = this.signatureMap.get(sigId);
			
			signatureMap.put(signature, value);
		}
		return signatureMap;
	}
	
	
	
	public Long getIdByEntry(String entry) {
		return this.entryToId.get(entry);
	}
	
	public String getEntryById(long id) {
		return this.idToEntry.get(id);
	}
	


	public Set<Long> getMsigIdByHash(String hash) {
		return hash64ToMsigId.get(hash);
	}

	
	public String getMolecularSignatureHashById(long msigId) {
		return this.msigIdToHash64.get(msigId);
	}
	
	public Set<Long> listMolecularSignatureIdBySignature(Signature signature) {
		return new HashSet<> (this.signatureToMsig.get(signature));
	}

	@Override
	public MolecularSignature getMolecularSignatureById(long msigId) {
		return this.msigMap.get(msigId);
	}
	
	public MolecularSignature getMolecularSignatureByHash(String msigHash) {
		Set<Long> msigIdSet = this.hash64ToMsigId.get(msigHash);
		if (msigIdSet == null || msigIdSet.isEmpty()) return null;
		long msigId = msigIdSet.iterator().next();
		return getMolecularSignatureById(msigId);
	}

	@Override
	public MolecularSignature getMolecularSignature(long cpdId, int h,
			boolean stereo) {
		return this.msigMap.get(cpdId);
	}

	@Override
	public void deleteMolecularSignature(long cpdId, int h, boolean stereo) {
		throw new RuntimeException("unsupported operation");
	}

	@Override
	public void saveMolecularSignature(long cpdId,
			MolecularSignature signatureSet) {
		throw new RuntimeException("unsupported operation");
	}

	@Override
	public Set<Long> getMoleculeReferencesBySignatureSet(
			MolecularSignature signatureSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getMoleculeReferencesBySignatureSetId(long signatureSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> findMolecularSignatureContainsAny(Set<Signature> signatures) {
		Set<Long> result = new HashSet<> ();
		for (Signature s : signatures) {
			//get signature sets that contain this signature node
			Set<Long> msigIds = this.signatureToMsig.get(s);
			result.addAll(msigIds);
		}
		
		return result;
	}

	@Override
	public Set<Long> findMolecularSignatureContainedIn(
			Map<Signature, Double> signatureMap) {
		Set<Signature> sigs = new HashSet<> (signatureMap.keySet());
		Set<Long> result = new HashSet<> ();
		Set<Long> a = findMolecularSignatureContainsAny(signatureMap.keySet());
		
		for (long sigSetId : a) {
			MolecularSignature msig = this.msigMap.get(sigSetId);
			Set<Signature> sigs_ = new HashSet<> (msig.getSignatureMap().keySet());
//			Node sigSetNode = graphDatabaseService.getNodeById(sigSetId);
//			for (Node sig : Neo4jUtils.collectNodeRelationshipNodes(sigSetNode, Neo4jSignatureRelationship.has_signature)) {
//				sigs_.add(new Signature((String) sig.getProperty("key")));
//			}
			
			if (sigs.containsAll(sigs_)) {
				//check values !
				result.add(sigSetId);
			}
		}
		
		return result;
	}



}
