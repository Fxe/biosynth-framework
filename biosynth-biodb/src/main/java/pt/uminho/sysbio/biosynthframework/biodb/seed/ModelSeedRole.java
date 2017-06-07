package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

public class ModelSeedRole {
  public String id;
  public String searchname;
  public String source;
  public String name;
  public List<String> reactions = new ArrayList<> ();
  public List<String> aliases = new ArrayList<> ();
  public List<String> complexes = new ArrayList<> ();
  public List<String> subsystems = new ArrayList<> ();
  public List<String> features = new ArrayList<> ();
}
