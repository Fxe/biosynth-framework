package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.util.HashMap;
import java.util.Map;

public class SignatureSet {
	
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
}
