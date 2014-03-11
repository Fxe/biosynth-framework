package edu.uminho.biosynth.core.components.biodb.chebi.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;

@Entity
@Table(name="chemical_data")
public class ChebiDumpMetaboliteChemicalDataEntity {
	
//	CREATE TABLE `chemical_data` (
//			  `id` INT NOT NULL,
//			  `compound_id` INT NOT NULL,
//			  `chemical_data` TEXT NOT NULL,
//			  `source` TEXT NOT NULL,
//			  `type` TEXT NOT NULL,
//			  PRIMARY KEY (`id`)
//			) ENGINE=InnoDB;
	
	@Id
	@Column(name="id", nullable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="compound_id")
	private ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity;
	
	@Column(name="chemical_data", nullable=false, length=255)
	private String chemicalData;
	
	@Column(name="source", nullable=false, length=32)
	private String source;
	
	@Column(name="type", nullable=false, length=32)
	private String type;
	
	public Integer getId() { return id;}
	public void setId(Integer id) { this.id = id;}

	public String getChemicalData() { return chemicalData;}
	public void setChemicalData(String chemicalData) { this.chemicalData = chemicalData;}

	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}

	public String getType() { return type;}
	public void setType(String type) { this.type = type;}

	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(chemicalData).append(sep);
		sb.append(source).append(sep);
		sb.append(type);
		sb.append(end);
		return sb.toString();
	}
}
