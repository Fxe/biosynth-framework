package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import pt.uminho.sysbio.biosynthframework.Reaction;

public abstract class ReactionToDotNodeTransformer<R extends Reaction>
implements VertexTransformer<R, DotNode>{

  @Override
  public abstract DotNode toDotNode(R reaction);

}
