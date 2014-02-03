package edu.uminho.biosynth.core.components;

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
		DATABASE, MODEL, ECNUMBER
	}
	
	@Id
    @Column(name="ID")
    @GeneratedValue
    protected Integer id;
	
	@Column(name="REFTYPE", length=15)
	@Enumerated(EnumType.STRING)
	protected Type type;
	
	@Column(name="REFSOURCE", length=255) protected String ref;
	@Column(name="REFVALUE", length=255) protected String value;
	
	public GenericCrossReference(GenericCrossReference.Type type, String reference, String value) {
		this.type = type;
		this.ref = reference;
		this.value = value;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

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
