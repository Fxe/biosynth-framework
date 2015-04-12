package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.util.Map;
import java.util.TreeMap;

public class ReactionSignature {
	
	private Long id;
	private int h;
	private boolean stereo = false;
	
	private Map<Signature, Double> leftSignatureMap = new TreeMap<>();
	private Map<Signature, Double> rightSignatureMap = new TreeMap<>();
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	public int getH() { return h;}
	public void setH(int h) { this.h = h;}
	
	public boolean isStereo() { return stereo;}
	public void setStereo(boolean stereo) {	this.stereo = stereo;}
	
	public Map<Signature, Double> getLeftSignatureMap() { return leftSignatureMap;}
	public void setLeftSignatureMap(Map<Signature, Double> leftSignatureMap) { this.leftSignatureMap = leftSignatureMap;}
	
	public Map<Signature, Double> getRightSignatureMap() { return rightSignatureMap;}
	public void setRightSignatureMap(Map<Signature, Double> rightSignatureMap) { this.rightSignatureMap = rightSignatureMap;}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof ReactionSignature)) return false;
		ReactionSignature sgs = (ReactionSignature) obj;
		
		//for now lets ignore stereo and h
		//sgs.h == this.h && sgs.stereo == this.stereo
		return  sgs.leftSignatureMap.equals(this.leftSignatureMap)
				&& sgs.rightSignatureMap.equals(this.rightSignatureMap);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
//		System.out.println(leftSignatureMap.equals(rightSignatureMap));
//		System.out.println(leftSignatureMap.hashCode() + " " + rightSignatureMap.hashCode());
		hash += leftSignatureMap.hashCode();
		hash += rightSignatureMap.hashCode();
		return hash;
	}
}
