package pt.uminho.sysbio.biosynth.integration.io.dao;

import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout;
import pt.uminho.sysbio.biosynthframework.io.BiosynthDao;

public interface MetabolicLayoutDao extends BiosynthDao<MetabolicLayout> {
  public void addNode(MetabolicLayout metabolicLayout, LayoutNode layoutNode);
  public void addEdge(LayoutNode from, LayoutNode to);
}
