package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name="integration")
public class IntegrationSet {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="name", nullable=false, length=255)
	private String name;
	
	@Column(name="description", nullable=true, length=255)
	private String description = "";
	
	@Column(name="last_cluster_entry", nullable=true, length=255)
	private String lastClusterEntry;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY , mappedBy="integrationSet")
//	@LazyCollection(LazyCollectionOption.EXTRA)
	@MapKey(name="id")
	@BatchSize(size=100) @JsonInclude
	private Map<Long, IntegratedCluster> integratedClustersMap = new HashMap<> ();

	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	public String getName() { return name;}
	public void setName(String name) { this.name = name;}
	
	public String getLastClusterEntry() { return lastClusterEntry;}
	public void setLastClusterEntry(String lastClusterEntry) { this.lastClusterEntry = lastClusterEntry;}
	
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
	public Map<Long, IntegratedCluster> getIntegratedClustersMap() {
		return integratedClustersMap;
	}
	public void setIntegratedClustersMap(
			Map<Long, IntegratedCluster> integratedClustersMap) {
		this.integratedClustersMap = integratedClustersMap;
	}
	
	public int size() {
		return this.integratedClustersMap.size();
	}
	
	@Override
	public String toString() {
		return String.format("IntegratedSet[%d] %s [%s]", id, name, description);
	}
}
