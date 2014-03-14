package edu.uminho.biosynth.core.components.biodb.kegg;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import edu.uminho.biosynth.core.components.GenericMetabolite;

@MappedSuperclass
public abstract class AbstractKeggMetabolite extends GenericMetabolite {

	private static final long serialVersionUID = -6305406455329893352L;

	@Column(name="K_COMMENT", length=2047) private String comment;
	@Column(name="REMARK", length=1023) private String remark;
	
	@Column(name="mol_file") private String mol2d;
	
	public String getComment() { return comment;}
	public void setComment(String comment) { this.comment = comment;}
	
	public String getRemark() { return remark;}
	public void setRemark(String remark) { this.remark = remark;}
	
	public String getMol2d() { return mol2d;}
	public void setMol2d(String mol2d) { this.mol2d = mol2d;}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("remark:").append(remark).append(sep);
		sb.append("comment:").append(comment).append(sep);
		sb.append("Has Mol File:").append(mol2d==null?false:true);
		return sb.toString();
	}
}
