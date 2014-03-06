package edu.uminho.biosynth.core.components.biodb.seed.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;

@Entity
@Table(name="SEED_COMPOUND_STRUCTURE")
public class SeedCompoundStructureEntity {
	
	@Id
    @Column(name="ID")
    @GeneratedValue
    private Integer id;
	
	@ManyToOne
	@JoinColumn(name="ID_METABOLITE")
	private SeedMetaboliteEntity seedCompoundEntity;
	
	@Column(name="STRUCTURE") private String structure;
	@Column(name="CKSUM") private String cksum;
	@Column(name="S_TYPE") private String type;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}
	
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	
	public String getCksum() {
		return cksum;
	}
	public void setCksum(String cksum) {
		this.cksum = cksum;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append("structure:").append(structure).append(sep);
		sb.append("cksum:").append(cksum).append(sep);
		sb.append("type:").append(type);
		sb.append(end);
		return sb.toString();
	}
}
