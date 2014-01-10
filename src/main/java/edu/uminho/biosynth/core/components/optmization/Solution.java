package edu.uminho.biosynth.core.components.optmization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name="SOLUTION")
public class Solution {
	
	@Id
    @Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected Integer id;
	public Integer getId() { return this.id; }
	public void setId(Integer id) { this.id = id; }
	
	@Column(name="DESCRIPTION")
	private String description;
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}

	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="CREATED_AT") private DateTime created_at;
	public DateTime getCreated_at() { return created_at;}
	public void setCreated_at(DateTime created_at) { this.created_at = created_at;}

	@ManyToOne
	@JoinColumn(name="ID_SOLUTIONSET")
	private SolutionSet solutionSet;
}
