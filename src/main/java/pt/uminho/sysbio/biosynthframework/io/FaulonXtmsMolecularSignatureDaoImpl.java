package pt.uminho.sysbio.biosynthframework.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;

public class FaulonXtmsMolecularSignatureDaoImpl implements MolecularSignatureDao {
	
	private Map<Long, Signature> signatureMap = new HashMap<> ();
	private Map<Long, MolecularSignature> msigMap = new HashMap<> ();
	private Map<String, Long> entryToId = new HashMap<> ();
	
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
				
				msigMap.put(i, msig);
				entryToId.put(cols[0].trim(), i);
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

	@Override
	public MolecularSignature getMolecularSignatureById(long msigId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MolecularSignature getMolecularSignature(long cpdId, int h,
			boolean stereo) {
		return this.msigMap.get(cpdId);
	}

	@Override
	public void deleteMolecularSignature(long cpdId, int h, boolean stereo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMolecularSignature(long cpdId,
			MolecularSignature signatureSet) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> findMolecularSignatureContainedIn(
			Map<Signature, Double> signatureMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
