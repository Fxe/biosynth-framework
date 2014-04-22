package edu.uminho.biosynth.core.components.optimization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="opt_solution")
public class HbmOptimizationSolution implements Solution {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="sid", nullable=false)
	private Long sid;
	
	@Column(name="value", nullable=false)
	private String solution;
	
	@Column(name="type", nullable=false)
	private String type;
	
	@ManyToOne
	@JoinColumn(name="opt_solution_set_id", nullable=false)
	private HbmOptimizationSolutionSet solutionSet;

	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;
	}
	
	

	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HbmOptimizationSolutionSet getSolutionSet() {
		return solutionSet;
	}

	public void setSolutionSet(HbmOptimizationSolutionSet solutionSet) {
		this.solutionSet = solutionSet;
	}

}
