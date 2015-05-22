package pt.uminho.sysbio.biosynthframework;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractBiosynthEntity implements Serializable {

	private static final long serialVersionUID = 353453463465587L;

	@Id
  @Column(name="id", nullable=false)
	@GeneratedValue
	protected Long id;
	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }

	@MetaProperty
	@Column(name="entry", unique=true, length=255, nullable=false)
	@XmlAttribute(name="entry")
	protected String entry;

	@MetaProperty
	@Column(name="e_name", length=2047)
	protected String name;
	
	@MetaProperty
	@Column(name="e_source", length=255)
	protected String source;
	
	@MetaProperty
	@Column(name="description", length=2047)
	protected String description = "";
	
	@JsonIgnore
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="created_at") private DateTime created_at;
	
	@JsonIgnore
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="updated_at") private DateTime updated_at;
	
	public AbstractBiosynthEntity() { }
	public AbstractBiosynthEntity(String entry) {
		this.entry = entry;
	}
	
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}
	
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
	
	public String getName() { return this.name;}
	public void setName(String name) { this.name = name;}
	
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}
	
	@JsonIgnore
	@JsonSerialize(using = DateTimeSerializer.class)
	public DateTime getCreatedAt() { return created_at;}
	
	@JsonIgnore
//	@JsonDeserialize(using = DateTimeDeserializer.class)
	public void setCreatedAt(String modDate) { this.created_at = new DateTime(modDate);}
	
	@JsonIgnore
	@JsonSerialize(using = DateTimeSerializer.class)
	public DateTime getUpdatedAt() { return this.updated_at;}
	
	@JsonIgnore
//	@JsonDeserialize(using = DateTimeDeserializer.class)
	public void setUpdatedAt(String modDate) { this.updated_at = new DateTime(modDate);}
	
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
