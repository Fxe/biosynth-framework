package edu.uminho.biosynth.core.components.biocyc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;

@Entity
@Table(name="BIOCYC_METABOLITE")
public class BioCycMetaboliteEntity extends GenericMetabolite {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MOLW") private double molWeight;
	@Column(name="INCHI") private String inChI;
	@Column(name="SMILES") private String smiles;
	@Column(name="GIBBS") private double gibbs;

	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("entry:").append(entry).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("mclass:").append(this.metaboliteClass).append(sep);
		sb.append("formula:").append(formula).append(sep);
		sb.append("source:").append(this.source).append(sep);
		sb.append("molWeight:").append(this.molWeight).append(sep);
		sb.append("gibbs:").append(this.gibbs).append(sep);
		sb.append("Smiles:").append(this.smiles).append(sep);
		sb.append("InChI:").append(this.inChI).append(sep);
		return sb.toString();
	}
}
