package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

public interface Neo4jDefinitions {

  /**
   * user defined unique key for entities
   */
  public final static String ENTITY_NODE_UNIQUE_CONSTRAINT = "entry";
  
  /**
   * user defined unique key for properties
   */
  public final static String PROPERTY_NODE_UNIQUE_CONSTRAINT = "key";
  
  /**
   * unique property label constraint
   */
  public final static String MAJOR_LABEL_PROPERTY = "major_label";
  
  /**
   * defines if the entity is a proxy (maybe exists)
   */
  public final static String PROXY_PROPERTY = "proxy";
  
  /**
   * object that references another object (by system id)
   */
  public final static String MEMBER_REFERENCE = "reference_id";
  
  /**
   * Redefines pathway
   */
  public final static String OVERRIDE_PATHWAY = "pathway";
  
  /**
   * Redefines compartment (direct link the SubcellularLocation)
   */
  public final static String OVERRIDE_COMPARTMENT = "scmp";
  
  /**
   * Alternative name (e.g., supplementary materials)
   */
  public final static String OVERRIDE_NAME = "supName";
  
  public final static String SHA256 = "sha256";
  
  /**
   * Object category
   */
  public final static String ENTITY_TYPE = "entityType";
  
  /**
   * Origin of the annotation
   */
  public final static String ANNOTATION_SOURCE = "source";
}
