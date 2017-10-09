package pt.uminho.sysbio.biosynthframework;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@MappedSuperclass
public class GenericCrossreference implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name="id")
  @GeneratedValue
  protected Long id;
  public Long getId() { return id;}
  public void setId(Long id) { this.id = id;}

  @MetaProperty(asString=true)
  @Column(name="ref_type", length=15, nullable=false)
  @Enumerated(EnumType.STRING)
  protected ReferenceType type;

  @MetaProperty
  @Column(name="tag", length=255, nullable=false) 
  protected String ref;

  @MetaProperty
  @Column(name="value", length=255, nullable=false)
  protected String value;

  public GenericCrossreference() { }

  public GenericCrossreference(ReferenceType type, String reference, String value) {
    this.type = type;
    this.ref = reference;
    this.value = value;
  }



  public ReferenceType getType() {
    return type;
  }
  public void setType(ReferenceType type) {
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
//    final char sep = ',';
//    final char ini = '<';
//    final char end = '>';
//    StringBuilder sb = new StringBuilder();
//    sb.append(ini);
//    sb.append(type).append(sep);
//    sb.append(ref).append(sep);
//    sb.append(value);
//    sb.append(end);
    return String.format("<%s, %s, %s>", type, ref, value);
  }
}
