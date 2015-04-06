package pt.uminho.sysbio.biosynthframework.chemanalysis;

public class Signature implements Comparable<Signature> {
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
	
	@Override
	public int hashCode() {
		return signature.hashCode();
	}
	
	@Override
	public String toString() {
		return signature;
	}


}
