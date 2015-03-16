package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="integration_set")
public class IntegrationSet extends AbstractBiosynthEntity {

	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue
//	@Column(name="id", nullable=false)
//	private Long id;
//	
//	@Column(name="entry", nullable=false, length=255, unique=true)
//	private String entry;
//	
//	@Column(name="description", nullable=true, length=255)
//	private String description = "";
	
	@Column(name="last_cluster_entry", nullable=true, length=255)
	private String lastClusterEntry;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY , mappedBy="integrationSet")
//	@LazyCollection(LazyCollectionOption.EXTRA)
	@MapKey(name="id")
	@BatchSize(size=100) @JsonIgnore
	private Map<Long, IntegratedCluster> integratedClustersMap = new HashMap<> ();

	public String getLastClusterEntry() { return lastClusterEntry;}
	public void setLastClusterEntry(String lastClusterEntry) { this.lastClusterEntry = lastClusterEntry;}
	
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
		return String.format("IntegratedSet[%d] %s [%s]", id, entry, description);
	}
}
