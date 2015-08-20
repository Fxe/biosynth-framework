package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public interface DotMetaboliteNodeGenerator<M extends Metabolite> {
	public String buildDotNodeString(M cpd);
}
