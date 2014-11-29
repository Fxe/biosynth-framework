package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@MappedSuperclass
public abstract class AbstractKeggMetabolite extends GenericMetabolite {

	private static final long serialVersionUID = -6305406455329893352L;

	@MetaProperty
	@Column(name="K_COMMENT", length=2047)
	private String comment;
	
	@MetaProperty
	@Column(name="REMARK", length=1023) 
	private String remark;
	
	@Column(name="mol_file", columnDefinition="text") private String mol2d;
	
	public String getComment() { return comment;}
	public void setComment(String comment) { this.comment = comment;}
	
	public String getRemark() { return remark;}
	public void setRemark(String remark) { this.remark = remark;}
	
	public String getMol2d() { return mol2d;}
	public void setMol2d(String mol2d) { this.mol2d = mol2d;}
	
	public List<String> getNames() {
		List<String> names = new ArrayList<> ();
		if (this.name != null) {
			for (String str : this.name.split("[\\s+]*;[\\s+]*")) {
				String s = str.trim(); 
				if (!s.isEmpty()) {
					names.add(s);
				}
			}
		}
		return names;
	}
	
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
