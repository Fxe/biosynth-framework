package edu.uminho.biosynth.core.components.biocyc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biocyc.components.BioCycReactionRightEntity;

@Entity
@Table(name="BIOCYC_REACTION")
public class BioCycReactionEntity extends GenericReaction {

	private static final long serialVersionUID = 1L;
	
	@Column(name="ENZYME") private String enzyme;
	public String getEnzyme() { return enzyme; }
	public void setEnzyme(String enzyme) { this.enzyme = enzyme;}
	
	@Column(name="ORIENTATION") private String direction;
	public String getDirection() { return direction;}
	public void setDirection(String direction) { this.direction = direction;}

	@OneToMany(mappedBy = "mnxReactionEntity", cascade = CascadeType.ALL)
	private List<BioCycReactionLeftEntity> left = new ArrayList<> ();
	public List<BioCycReactionLeftEntity> getLeft() { return left;}
	public void setLeft(List<BioCycReactionLeftEntity> left) { this.left = left;}

	@OneToMany(mappedBy = "mnxReactionEntity", cascade = CascadeType.ALL)
	private List<BioCycReactionRightEntity> right = new ArrayList<> ();
	public List<BioCycReactionRightEntity> getRight() { return right;}
	public void setRight(List<BioCycReactionRightEntity> right) { this.right = right;}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
//		sb.append("molWeight:").append(this.molWeight).append(sep);
//		sb.append("gibbs:").append(this.gibbs).append(sep);
//		sb.append("Smiles:").append(this.smiles).append(sep);
//		sb.append("InChI:").append(this.inChI).append(sep);
		return sb.toString();
	}
}
