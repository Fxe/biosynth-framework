package edu.uminho.biosynth.core.data.integration.chimera.domain.components;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

@Entity
@Table(name="integrated_metabolite_source")
public class IntegratedMetaboliteSourceProxy {

	@Id
	@Column(name="id")
	public Long id;
	
	@Column(name="entry")
	public String entry;
	
	@Column(name="major", nullable=false, length=255)
	public String majorLabel;
	
	public String webUrl = "#";
	
	public Set<String> labels;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	public IntegratedMetaboliteEntity integratedMetaboliteEntity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntry() {
		return entry;
	}

	public String getMajorLabel() {
		return majorLabel;
	}

	public void setMajorLabel(String majorLabel) {
		this.majorLabel = majorLabel;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
	
	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}
	
	



	public IntegratedMetaboliteEntity getIntegratedMetaboliteEntity() {
		return integratedMetaboliteEntity;
	}

	public void setIntegratedMetaboliteEntity(
			IntegratedMetaboliteEntity integratedMetaboliteEntity) {
		this.integratedMetaboliteEntity = integratedMetaboliteEntity;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(entry).append(sep);
		sb.append(webUrl).append(sep);
		sb.append(majorLabel).append(sep);
		sb.append(labels);
		sb.append(end);
		return sb.toString();
	}
}
