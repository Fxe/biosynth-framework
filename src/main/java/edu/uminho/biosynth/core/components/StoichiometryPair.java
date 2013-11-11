package edu.uminho.biosynth.core.components;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@MappedSuperclass
public class StoichiometryPair<M extends GenericMetabolite> {
	
    @Id
    @Column(name="ID")
    @GeneratedValue
	private Integer id;
    
    @Column(name="COEFFIC") protected Integer value;
	
	@OneToMany(mappedBy = "s", fetch = FetchType.LAZY)
	protected M genericMetabolite;
	
	@ManyToOne
	@JoinColumn(name="ID_REACTION")
	protected GenericReaction<M> genericReaction;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public M getGenericMetabolite() {
		return genericMetabolite;
	}
	public void setGenericMetabolite(M genericMetabolite) {
		this.genericMetabolite = genericMetabolite;
	}

	public GenericReaction<M> getGenericReaction() {
		return genericReaction;
	}
	public void setGenericReaction(GenericReaction<M> genericReaction) {
		this.genericReaction = genericReaction;
	}
	
	
}
