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
	protected Integer key;
	
	@Column(name="ENTRY")
	private String id;

	@Column(name="NAME")
	protected String name;
	
	@Column(name="SOURCE")
	protected String source;
	
	@Column(name="DESCRIPTION")
	protected String description;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="CREATED_AT") private DateTime created_at;
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="UPDATED_AT") private DateTime updated_at;
	
	public AbstractGenericEntity() {
		this.id = null;
	}
	public AbstractGenericEntity(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	public int getKey() {
		return this.key;
	}
	public void setKey(int key) {
		this.key = key;
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
	public void setpdatedAt(String modDate) {
		this.updated_at = new DateTime(modDate);
	}
}
