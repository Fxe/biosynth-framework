package pt.uminho.sysbio.biosynthframework;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@MappedSuperclass
public class StoichiometryPair implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name="id")
    @GeneratedValue
	protected Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	@MetaProperty
    @Column(name="value") protected double stoichiometry;
	public double getStoichiometry() { return stoichiometry;}
	public void setStoichiometry(double stoichiometry) { this.stoichiometry = stoichiometry;}
	
    @Column(name="metabolite_id") 
    protected Integer cpdKey;
    @MetaProperty
    @Column(name="metabolite_entry", nullable=false) 
    protected String cpdEntry;

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
	
	@Override
	public String toString() {
		return String.format("<%s,%s>", this.cpdEntry, this.stoichiometry);
	}
}
