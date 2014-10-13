package pt.uminho.sysbio.biosynthframework;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class GenericCrossReference implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Type {
		DATABASE, MODEL, ECNUMBER, NAME, OBSOLETE, SELF, PATENT, GENE, UNKNOWN, REACTION, CITATION
	}
	
	@Id
    @Column(name="id")
    @GeneratedValue
    protected Long id;
	
	@Column(name="ref_type", length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	protected Type type;
	
	@Column(name="tag", length=255, nullable=false) protected String ref;
	@Column(name="value", length=255, nullable=false) protected String value;
	
	public GenericCrossReference() { }
	
	public GenericCrossReference(GenericCrossReference.Type type, String reference, String value) {
		this.type = type;
		this.ref = reference;
		this.value = value;
	}
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}

	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
		sb.append(value);
		sb.append(end);
		return sb.toString();
	}
}
