package edu.uminho.biosynth.core.components.optimization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="opt_solution_set")
public class HbmOptimizationSolutionSet implements SolutionSet<HbmOptimizationSolution> {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="ssid", nullable=false, unique=true)
	private String ssid;
	
	@Column(name="ss_desc", nullable=true)
	private String description;
	
	@Column(name="algorithm", nullable=false)
	private String algorithm;
	
	@OneToMany(mappedBy="solutionSet", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
	private List<HbmOptimizationSolution> optimizationSolutions = new ArrayList<> ();;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public List<HbmOptimizationSolution> getOptimizationSolutions() {
		return optimizationSolutions;
	}

	public void setOptimizationSolutions(
			List<HbmOptimizationSolution> optimizationSolutions) {
		this.optimizationSolutions = optimizationSolutions;
	}

	@Override
	public void add(HbmOptimizationSolution solution) {
		solution.setSolutionSet(this);
		this.optimizationSolutions.add(solution);
	}

	@Override
	public void remove(HbmOptimizationSolution solution) {
		this.optimizationSolutions.remove(solution);
	}

	@Override
	public void size() {
		this.optimizationSolutions.size();
	}

	@Override
	public void clear() {
		this.optimizationSolutions.clear();
	}

	@Override
	public HbmOptimizationSolution get(Long id) {
		return this.optimizationSolutions.get(Integer.parseInt(id.toString()));
	}

}
