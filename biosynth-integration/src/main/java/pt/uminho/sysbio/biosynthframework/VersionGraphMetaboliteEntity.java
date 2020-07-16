package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class VersionGraphMetaboliteEntity extends AbstractBiosynthEntity {
  
  private static final long serialVersionUID = 1L;

  protected String namespace;
  
  protected Map<String, Map<String, Object>> properties = new HashMap<> ();
  protected int degree;
  protected int reactionDegree;
  
  
}
