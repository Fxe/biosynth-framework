package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="SEED_COMPOUND_PK")
public class SeedCompoundPkEntity {
	
	@Id
    @Column(name="id")
    @GeneratedValue
    private Integer id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private SeedMetaboliteEntity seedCompoundEntity;
	
	@Column(name="PK") private Double pk;
	@Column(name="ATOM") private short atom;
	@Column(name="PK_TYPE") private String type;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="MODDATE") private DateTime modDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}
	
	public double getPk() {
		return pk;
	}
	public void setPk(double pk) {
		this.pk = pk;
	}
	
	public short getAtom() {
		return atom;
	}
	public void setAtom(short atom) {
		this.atom = atom;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public DateTime getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = new DateTime(modDate);
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append("pk:").append(pk).append(sep);
		sb.append("atom:").append(atom).append(sep);
		sb.append("modDate:").append(modDate).append(sep);
		sb.append("type:").append(type);
		sb.append(end);
		return sb.toString();
	}
}
