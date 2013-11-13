package edu.uminho.biosynth.core.components;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@MappedSuperclass
public abstract class AbstractGenericEntity implements Serializable {

	private static final long serialVersionUID = 353453463465587L;

	@Id
    @Column(name="ID")
    @GeneratedValue
	protected Integer id;
	
	@Column(name="ENTRY", unique = true)
	private String entry;

	@Column(name="E_NAME")
	protected String name = "";
	
	@Column(name="E_SOURCE")
	protected String source;
	
	@Column(name="DESCRIPTION")
	protected String description = "";
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="CREATED_AT") private DateTime created_at;
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="UPDATED_AT") private DateTime updated_at;
	
	public AbstractGenericEntity() {
		this.entry = null;
	}
	public AbstractGenericEntity(String entry) {
		this.entry = entry;
	}
	
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public DateTime getCreatedAt() {
		return created_at;
	}
	public void setCreatedAt(String modDate) {
		this.created_at = new DateTime(modDate);
	}
	
	public DateTime getUpdatedAt() {
		return this.updated_at;
	}
	public void setUpdatedAt(String modDate) {
		this.updated_at = new DateTime(modDate);
	}
}
