package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Column;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class SeedCompartmentEntity {

//	private static final long serialVersionUID = 1L;

	@MetaProperty
    @Column(name="locked")
    private Short locked;
	
	@MetaProperty
	@Column(name="hierarchy")
	private Short hierarchy;
	
	@MetaProperty
    @Column(name="uuid") 
	private String uuid;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="MODDATE") private DateTime modDate;
	
	@MetaProperty
    @Column(name="id")
	private String id;
	
	@MetaProperty
    @Column(name="name")
	private String name;

	public Short getLocked() {
		return locked;
	}

	public void setLocked(Short locked) {
		this.locked = locked;
	}

	public Short getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(Short hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DateTime getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = new DateTime(modDate);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
