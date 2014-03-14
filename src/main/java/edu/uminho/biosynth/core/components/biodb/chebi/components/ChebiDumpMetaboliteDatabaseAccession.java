package edu.uminho.biosynth.core.components.biodb.chebi.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;

@Entity
@Table(name="database_accession")
public class ChebiDumpMetaboliteDatabaseAccession {
	
	@Id
	@Column(name="id", nullable=false)
	private long id;
	public long getId() { return id;}
	public void setId(long id) { this.id = id;}
	
	@ManyToOne
	@JoinColumn(name="compound_id")
	private ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity;
	public ChebiDumpMetaboliteEntity getChebiDumpMetaboliteEntity() { return chebiDumpMetaboliteEntity;}
	public void setChebiDumpMetaboliteEntity(
			ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity) {
		this.chebiDumpMetaboliteEntity = chebiDumpMetaboliteEntity;}
	
	@Column(name="accession_number", length=255)
	private String accessionNumber;
	public String getAccessionNumber() { return accessionNumber;}
	public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber;}
	
	@Column(name="type", length=255)
	private String type;
	public String getType() { return type;}
	public void setType(String type) { this.type = type;}
	
	@Column(name="source", length=255)
	private String source;
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}

	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(accessionNumber).append(sep);
		sb.append(type).append(sep);
		sb.append(source);
		sb.append(end);
		return sb.toString();
	}
}
