package pt.uminho.sysbio.biosynthframework.core.components.optimization;

import java.util.ArrayList;
import java.util.List;

public class SimpleOptimizationSolutionSet implements SolutionSet<SimpleOptimizationSolution> {

	private List<SimpleOptimizationSolution> solutions = new ArrayList<> ();
	
	@Override
	public void add(SimpleOptimizationSolution solution) {
		this.solutions.add(solution);
	}

	@Override
	public void remove(SimpleOptimizationSolution solution) {
		this.solutions.remove(solution);
	}

	@Override
	public void size() {
		this.solutions.size();
	}

	@Override
	public void clear() {
		this.solutions.clear();
	}

	@Override
	public SimpleOptimizationSolution get(Long id) {
		return this.solutions.get(Integer.parseInt(id.toString()));
	}
}
