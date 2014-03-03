package edu.uminho.biosynth.core.components;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractGenericEntity implements Serializable {

	private static final long serialVersionUID = 353453463465587L;

	@Id
    @Column(name="ID", nullable=false)
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(generator="IdOrGenerated", strategy=GenerationType.IDENTITY)
	@GenericGenerator(name="IdOrGenerated", strategy="edu.uminho.biosynth.core.components.AbstractEntityIdGenerator")
	@XmlAttribute(name="id")
	protected Integer id;
	public Integer getId() { return this.id; }
	public void setId(Integer id) { this.id = id; }
	
	@Column(name="ENTRY", unique=true, length=255, nullable=false)
	@XmlAttribute(name="entry")
	protected String entry;

	@Column(name="E_NAME", length=2047)
	protected String name = "";
	
	@Column(name="E_SOURCE", length=255)
	protected String source;
	
	@Column(name="DESCRIPTION", length=2047)
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
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("Id:").append(this.id).append(sep);
		sb.append("Entry:").append(this.entry).append(sep);
		sb.append("Name:").append(this.name).append(sep);
		sb.append("Description:").append(this.description).append(sep);
		sb.append("Source:").append(this.source);
		return sb.toString();
	}
}
