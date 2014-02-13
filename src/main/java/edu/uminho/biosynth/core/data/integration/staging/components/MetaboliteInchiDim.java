package edu.uminho.biosynth.core.data.integration.staging.components;

// Generated 13-Feb-2014 16:16:06 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * MetaboliteInchiDim generated by hbm2java
 */
@Entity
@Table(name = "metabolite_inchi_dim", uniqueConstraints = @UniqueConstraint(columnNames = "inchi"))
public class MetaboliteInchiDim implements java.io.Serializable {

	private int id;
	private String inchi;
	private Set<MetaboliteStga> metaboliteStgas = new HashSet<MetaboliteStga>(0);

	public MetaboliteInchiDim() {
	}

	public MetaboliteInchiDim(int id) {
		this.id = id;
	}

	public MetaboliteInchiDim(int id, String inchi,
			Set<MetaboliteStga> metaboliteStgas) {
		this.id = id;
		this.inchi = inchi;
		this.metaboliteStgas = metaboliteStgas;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "inchi", unique = true, length = 65534)
	public String getInchi() {
		return this.inchi;
	}

	public void setInchi(String inchi) {
		this.inchi = inchi;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "metaboliteInchiDim")
	public Set<MetaboliteStga> getMetaboliteStgas() {
		return this.metaboliteStgas;
	}

	public void setMetaboliteStgas(Set<MetaboliteStga> metaboliteStgas) {
		this.metaboliteStgas = metaboliteStgas;
	}

}
