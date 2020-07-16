package pt.uminho.sysbio.biosynthframework.sbml.reader;

import pt.uminho.sysbio.biosynthframework.Tuple2;

public interface XmlSbmlModelBoundReader {
  public Tuple2<String> getReactionBounds(String id);
}
