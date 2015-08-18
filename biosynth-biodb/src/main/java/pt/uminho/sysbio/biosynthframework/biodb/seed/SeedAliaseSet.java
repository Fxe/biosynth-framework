package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="seed_aliase_set")
public class SeedAliaseSet {
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="source")
	private String source;
	
	@Column(name="attribute")
	private String attribute;
	
	@Column(name="name")
	private String name;
	
	@Column(name="mod_date")
	private String modDate;
	
	@Column(name="type")
	private String type;
	
	@Column(name="uuid")
	private String uuid;
	
	public Map<String, Set<String>> uuidToAliase = new HashMap<> ();

	
	public Map<String, Set<String>> getUuidToAliase() {
		return uuidToAliase;
	}
	public void addUuidToAliase(String uuid, String aliase) {
		if (!uuidToAliase.containsKey(uuid)) 
			uuidToAliase.put(uuid, new HashSet<String>());
		uuidToAliase.get(uuid).add(aliase);
//		if (prev != null) System.err.println(String.format("UUID: %s as multiple aliases: %s %s", uuid, aliase, prev));
	}
	public void setUuidToAliase(Map<String, Set<String>> uuidToAliase) {
		this.uuidToAliase = uuidToAliase;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = modDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
}