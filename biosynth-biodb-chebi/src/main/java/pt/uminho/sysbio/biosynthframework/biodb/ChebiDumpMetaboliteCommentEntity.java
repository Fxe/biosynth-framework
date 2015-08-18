package pt.uminho.sysbio.biosynthframework.biodb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name="comments")
public class ChebiDumpMetaboliteCommentEntity {
//	CREATE TABLE `comments` (
//			  `id` INT NOT NULL,
//			  `compound_id` INT NOT NULL,
//			  `text` TEXT NOT NULL,
//			  `created_on` DATETIME NOT NULL,
//			  `datatype` VARCHAR(80),
//			  `datatype_id` INT NOT NULL,
//			  PRIMARY KEY (`id`)
//			) ENGINE=InnoDB;
	@Id
	@Column(name="id", nullable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="compound_id")
	private ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity;
	
	@Column(name="text", nullable=false)
	private String text;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="created_on") private DateTime created_at;
	
	@Column(name="datatype", length=80)
	private String dataType;
	
	@Column(name="datatype_id", nullable=false)
	private Integer dataTypeId;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}



	public String getDataType() {
		return dataType;
	}



	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public Integer getDataTypeId() {
		return dataTypeId;
	}



	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}



	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(text).append(sep);
		sb.append(created_at).append(sep);
		sb.append(dataType).append(sep);
		sb.append(dataTypeId);
		sb.append(end);
		return sb.toString();
	}
}
