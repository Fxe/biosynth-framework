package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.io.Serializable;

import pt.uminho.sysbio.biosynthframework.util.DigestUtils;

public class Signature implements Comparable<Signature>, Serializable {

	private static final long serialVersionUID = 1987L;

	private long id;

	private final String signature;
	
	public Signature(String signature) {
		this.signature = signature;
	}
	
	public long getId() { return id;}
	public void setId(long id) { this.id = id;}
	
	public String getSignature() {
		return signature;
	}
	
	@Override
	public int compareTo(Signature o) {
		return signature.compareTo(o.signature);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Signature)) return false;
		return signature.equals(((Signature) obj).signature);
	}
	
	public long hash() {
		return DigestUtils.hash(signature);
	}
	
	public char getPivotAtom() {
	  return this.signature.charAt(1);
	}
	
	@Override
	public int hashCode() {
//		System.out.println(signature + " " + signature.hashCode());
		return signature.hashCode();
	}
	
	@Override
	public String toString() {
		return signature;
	}
}
