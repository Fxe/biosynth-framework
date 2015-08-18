package pt.uminho.sysbio.biosynthframework.biodb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="structures")
public class ChebiDumpMetaboliteStructuresEntity {
//	CREATE TABLE `structures` (
//			  `id` INT NOT NULL,
//			  `compound_id` INT NOT NULL,
//			  `structure` TEXT NOT NULL,
//			  `type` TEXT NOT NULL,
//			  `dimension` TEXT NOT NULL,
//			  PRIMARY KEY (`id`)
//			) ENGINE=InnoDB;
	@Id
	@Column(name="id", nullable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="compound_id")
	private ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity;
	
	@Column(name="structure", nullable=false)
	private String structure;
	
	@Column(name="type", nullable=false)
	private String type;
	
	@Column(name="dimension", nullable=false, length=4)
	private String dimension;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getStructure() {
		return structure;
	}



	public void setStructure(String structure) {
		this.structure = structure;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getDimension() {
		return dimension;
	}



	public void setDimension(String dimension) {
		this.dimension = dimension;
	}



	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(type).append(sep);
		sb.append(dimension);
		sb.append(end);
		return sb.toString();
	}
}
