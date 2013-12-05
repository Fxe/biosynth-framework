package edu.uminho.biosynth.core.components.biocyc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;

@Entity
@Table(name="BIOCYC_METABOLITE")
public class BioCycMetaboliteEntity extends GenericMetabolite {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MOLW") private double molWeight;
	public double getMolWeight() { return molWeight;}
	public void setMolWeight(double molWeight) { this.molWeight = molWeight;}

	@Column(name="INCHI") private String inChI;
	public String getInChI() { return inChI;}
	public void setInChI(String inChI) { this.inChI = inChI;}

	@Column(name="SMILES") private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}

	@Column(name="GIBBS") private double gibbs;
	public double getGibbs() { return gibbs;}
	public void setGibbs(double gibbs) { this.gibbs = gibbs;}
	
	@Column(name="CHARGE")
	private int charge;
	public int getCharge() { return charge; }
	public void setCharge(int charge) { this.charge = charge; }
	
	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_REACTION", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="REACTION")
	protected List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("molWeight:").append(this.molWeight).append(sep);
		sb.append("gibbs:").append(this.gibbs).append(sep);
		sb.append("Smiles:").append(this.smiles).append(sep);
		sb.append("InChI:").append(this.inChI).append(sep);
		sb.append("Reactions:").append(this.reactions);
		return sb.toString();
	}
}
