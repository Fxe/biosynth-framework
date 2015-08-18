package pt.uminho.sysbio.biosynthframework.util.math.components;

public class OrderedPair<T1, T2> {
	private T1 first;
	private T2 second;
	
	public OrderedPair() { }
	public OrderedPair(T1 first, T2 second) { 
		this.first = first;
		this.second = second;
	}
	
	public T1 getFirst() {
		return first;
	}
	public void setFirst(T1 first) {
		this.first = first;
	}
	public T2 getSecond() {
		return second;
	}
	public void setSecond(T2 second) {
		this.second = second;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) return false;
		OrderedPair<?, ?> aux = (OrderedPair<?, ?>) obj;
		if ( aux.first == null && aux.second == null) {
			return this.first == null && this.second == null;
		}
		if ( aux.first == null && aux.second != null) {
			return this.first == null && aux.second.equals(this.second);
		}
		if ( aux.first != null && aux.second == null) {
			return aux.first.equals(this.first) && this.second == null;
		}
		return aux.first.equals(this.first) && aux.second.equals(this.second);
	};
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = hash * 13 * first.hashCode();
		hash = hash * 13 * second.hashCode();
		return hash;
	}
	
	public String toString() {
		return String.format("<%s, %s>", this.first == null ? "null" : this.first, 
				this.second == null ? "null" : this.second);
	};
}
