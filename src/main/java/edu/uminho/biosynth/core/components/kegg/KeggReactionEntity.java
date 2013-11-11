package edu.uminho.biosynth.core.components.kegg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericReaction;

@Entity
@Table(name="KEGG_REACTION")
public class KeggReactionEntity extends GenericReaction<KeggMetaboliteEntity>{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DEFINITION") private String definition;
	@Column(name="COMMENT") private String comment;
	@Column(name="REMARK") private String remark;
	
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
