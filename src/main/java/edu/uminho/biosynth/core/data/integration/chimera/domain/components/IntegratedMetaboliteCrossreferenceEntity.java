package edu.uminho.biosynth.core.data.integration.chimera.domain.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

@Entity
@Table(name="integrated_metabolite_crossreference")
public class IntegratedMetaboliteCrossreferenceEntity extends GenericCrossReference {
	
	private static final long serialVersionUID = -5148544499444983334L;

	@Column(name="source", nullable=false)
	private String source;
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private IntegratedMetaboliteEntity integratedMetaboliteEntity;
	
	public IntegratedMetaboliteEntity getIntegratedMetaboliteEntity() {
		return integratedMetaboliteEntity;
	}
	public void setIntegratedMetaboliteEntity(
			IntegratedMetaboliteEntity integratedMetaboliteEntity) {
		this.integratedMetaboliteEntity = integratedMetaboliteEntity;
	}
	
	public IntegratedMetaboliteCrossreferenceEntity() { }
	public IntegratedMetaboliteCrossreferenceEntity(GenericCrossReference xref) {
		super(xref.getType(), xref.getRef(), xref.getValue());
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(type).append(sep);
		sb.append(ref).append(sep);
		sb.append(value).append(sep);
		sb.append(source);
		sb.append(end);
		return sb.toString();
	}
}
