package pt.uminho.sysbio.biosynthframework.biodb.biocyc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="biocyc_reaction_crossref")
public class BioCycReactionCrossReferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 2935318839395889046L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private BioCycReactionEntity bioCycReactionEntity;
	public BioCycReactionEntity getBioCycReactionEntity() { return bioCycReactionEntity; }
	public void setBioCycReactionEntity(BioCycReactionEntity bioCycReactionEntity) {
		this.bioCycReactionEntity = bioCycReactionEntity;
	}
	
	@MetaProperty
	@Column(name="relationship") private String relationship;
	public String getRelationship() { return relationship;}
	public void setRelationship(String relationship) { this.relationship = relationship;}
	
	@MetaProperty
	@Column(name="url") private String url;
	public String getUrl() { return url;}
	public void setUrl(String url) { this.url = url;}
	
	public BioCycReactionCrossReferenceEntity() { super(null, null, null); }
	public BioCycReactionCrossReferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
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
		sb.append(relationship).append(sep);
		sb.append(url);
		sb.append(end);
		return sb.toString();
	}
}
