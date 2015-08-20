package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public abstract class MetaboliteToDotNodeTransformer<M extends Metabolite> 
implements VertexTransformer<M, DotNode> {
  
  @Override
  public abstract DotNode toDotNode(M cpd);

}
