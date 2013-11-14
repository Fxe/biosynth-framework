package edu.uminho.biosynth.core.components;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class StoichiometryPair {
	
    @Id
    @Column(name="ID")
    @GeneratedValue
	private Integer id;
    
    @Column(name="COEFFIC") protected double value;
    @Column(name="ID_METABOLITE") protected Integer cpdKey;
    @Column(name="ENTRY_METABOLITE") protected String cpdEntry;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCpdKey() {
		return cpdKey;
	}
	public void setCpdKey(Integer cpdKey) {
		this.cpdKey = cpdKey;
	}
	
	public String getCpdEntry() {
		return cpdEntry;
	}
	public void setCpdEntry(String cpdEntry) {
		this.cpdEntry = cpdEntry;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
