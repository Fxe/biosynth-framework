package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.util.HashMap;
import java.util.Map;

public class MolecularSignature {
	
	private Long id;
	private int h;
	private boolean stereo = false;
	
	private Map<Signature, Double> signatureMap = new HashMap<>();
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	public int getH() { return h;}
	public void setH(int h) { this.h = h;}
	
	public boolean isStereo() { return stereo;}
	public void setStereo(boolean stereo) {	this.stereo = stereo;}
	
	public Map<Signature, Double> getSignatureMap() {
		return signatureMap;
	}

	public void setSignatureMap(Map<Signature, Double> signatureMap) {
		this.signatureMap = signatureMap;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof MolecularSignature)) return false;
		MolecularSignature sgs = (MolecularSignature) obj;
		
		//for now lets ignore stereo and h
		//sgs.h == this.h && sgs.stereo == this.stereo
		return  sgs.signatureMap.equals(this.signatureMap);
	}
	
	@Override
	public int hashCode() {
		return this.signatureMap.hashCode();
	}
}
