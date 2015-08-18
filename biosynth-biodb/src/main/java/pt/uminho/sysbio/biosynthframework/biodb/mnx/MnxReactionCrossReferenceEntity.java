package pt.uminho.sysbio.biosynthframework.biodb.mnx;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="MNX_REACTION_CROSSREF")
public class MnxReactionCrossReferenceEntity extends GenericCrossreference {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private MnxReactionEntity mnxReactionEntity;
	
	public MnxReactionCrossReferenceEntity() {
		super(null, null, null);
	}
	public MnxReactionCrossReferenceEntity(ReferenceType type, String reference,
			String value) {
		super(type, reference, value);
	}
	
	public MnxReactionEntity getMnxReactionEntity() {
		return mnxReactionEntity;
	}
	public void setMnxReactionEntity(MnxReactionEntity mnxReactionEntity) {
		this.mnxReactionEntity = mnxReactionEntity;
	}
}
