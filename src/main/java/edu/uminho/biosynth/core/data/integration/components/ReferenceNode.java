package edu.uminho.biosynth.core.data.integration.components;

import java.util.HashSet;
import java.util.Set;

public class ReferenceNode {

	private Integer id;
	private Set<OrderedPair<Integer, String>> idServiceMap = new HashSet<> ();
	private OrderedPair<String, Class<?>> entryTypePair = new OrderedPair<>();

	public Integer getId() { return id;}
	public void setId(Integer id) { this.id = id; }
	
	public ReferenceNode(String value, Class<?> clazz) {
		this.entryTypePair.setFirst(value);
		this.entryTypePair.setSecond(clazz);
	}
	
	public OrderedPair<String, Class<?>> getEntryTypePair() {
		return entryTypePair;
	}

	public void setEntryTypePair(OrderedPair<String, Class<?>> entryTypePair) {
		this.entryTypePair = entryTypePair;
	}

	public String getEntry() {
		return entryTypePair.getFirst();
	}

	public void setEntry(String entry) {
		this.entryTypePair.setFirst(entry);
	}

	public Class<?> getEntityType() {
		return entryTypePair.getSecond();
	}
	
	public Set<String> getRelatedServiceIds() {
		Set<String> serviceIdSet = new HashSet<> ();
		for (OrderedPair<Integer, String> pair : idServiceMap) {
			serviceIdSet.add(pair.getSecond());
		}
		
		return serviceIdSet;
	}

	public void setEntityType(Class<?> entityType) {
		this.entryTypePair.setSecond(entityType);
	}
	
	public void addIdServicePair(Integer id, String serviceName) {
		OrderedPair<Integer, String> pair = new OrderedPair<Integer, String>(id, serviceName);
		this.idServiceMap.add(pair);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) return false;
		ReferenceNode aux = (ReferenceNode) obj;
		if (aux.entryTypePair == null) {
			return aux.entryTypePair == null && this.entryTypePair == null;
		}
		return aux.entryTypePair.equals(this.entryTypePair);
	};
	
	@Override
	public int hashCode() {
		return this.entryTypePair.hashCode();
	};
	
	@Override
	public String toString() {
		return String.format("%s => %s", this.entryTypePair.getFirst(), this.idServiceMap);
	}
}
