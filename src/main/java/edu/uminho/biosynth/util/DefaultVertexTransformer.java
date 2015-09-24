package edu.uminho.biosynth.util;

public class DefaultVertexTransformer implements IVertexTransformer<Object> {

	@Override
	public DotNode toDotNode(Object vertex) {
		DotNode node = new DotNode();
		node.setLabel(vertex.toString());
		return node;
	}

}
