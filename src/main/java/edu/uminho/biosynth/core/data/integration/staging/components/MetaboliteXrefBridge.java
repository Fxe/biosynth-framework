package edu.uminho.biosynth.core.data.integration.staging.components;

// Generated 13-Feb-2014 16:16:06 by Hibernate Tools 4.0.0

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * MetaboliteXrefBridge generated by hbm2java
 */
@Entity
@Table(name = "metabolite_xref_bridge")
public class MetaboliteXrefBridge implements java.io.Serializable {

	private MetaboliteXrefBridgeId id;
	private MetaboliteXrefGroupDim metaboliteXrefGroupDim;
	private MetaboliteXrefDim metaboliteXrefDim;

	public MetaboliteXrefBridge() {
	}

	public MetaboliteXrefBridge(MetaboliteXrefBridgeId id,
			MetaboliteXrefGroupDim metaboliteXrefGroupDim,
			MetaboliteXrefDim metaboliteXrefDim) {
		this.id = id;
		this.metaboliteXrefGroupDim = metaboliteXrefGroupDim;
		this.metaboliteXrefDim = metaboliteXrefDim;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "xrefGroupId", column = @Column(name = "xref_group_id", nullable = false)),
			@AttributeOverride(name = "xrefId", column = @Column(name = "xref_id", nullable = false)) })
	public MetaboliteXrefBridgeId getId() {
		return this.id;
	}

	public void setId(MetaboliteXrefBridgeId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "xref_group_id", nullable = false, insertable = false, updatable = false)
	public MetaboliteXrefGroupDim getMetaboliteXrefGroupDim() {
		return this.metaboliteXrefGroupDim;
	}

	public void setMetaboliteXrefGroupDim(
			MetaboliteXrefGroupDim metaboliteXrefGroupDim) {
		this.metaboliteXrefGroupDim = metaboliteXrefGroupDim;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "xref_id", nullable = false, insertable = false, updatable = false)
	public MetaboliteXrefDim getMetaboliteXrefDim() {
		return this.metaboliteXrefDim;
	}

	public void setMetaboliteXrefDim(MetaboliteXrefDim metaboliteXrefDim) {
		this.metaboliteXrefDim = metaboliteXrefDim;
	}

}
