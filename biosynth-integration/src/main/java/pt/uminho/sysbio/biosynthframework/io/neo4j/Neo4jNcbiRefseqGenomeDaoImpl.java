package pt.uminho.sysbio.biosynthframework.io.neo4j;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbNode;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.RefseqGenomeEntity;
import pt.uminho.sysbio.biosynthframework.neo4j.GenomeDatabase;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jNcbiRefseqGenomeDaoImpl extends AbstractNeo4jBiosDao<RefseqGenomeEntity>{
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jNcbiRefseqGenomeDaoImpl.class);
  
  protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
      new AnnotationPropertyContainerBuilder();
  
  public Neo4jNcbiRefseqGenomeDaoImpl(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService, GenomeDatabase.RefSeqGenome);
  }

  public static RefseqGenomeEntity convert(EutilsAssemblyObject o) {
    RefseqGenomeEntity entity = new RefseqGenomeEntity();
    String entry = o.assemblyaccession;
    if (!entry.startsWith("GCF")) {
      logger.warn("rejected must be a GCF genome");
      return null;
    }
    entity.setEntry(entry);
    entity.setAssemblyName(o.assemblyname);
    entity.setBiosampleAccession(o.biosampleaccn);
    entity.setBiosampleId(o.biosampleid);
    entity.setOrganism(o.organism);
    if (!DataUtils.empty(o.partialgenomerepresentation)) {
      entity.setPartialGenomeRepresentation(Boolean.parseBoolean(o.partialgenomerepresentation));
    }
    entity.setRefseqCategory(o.refseq_category);
    entity.setSubmitterOrganization(o.submitterorganization);
    if (!DataUtils.empty(o.taxid)) {
      entity.setTxid("txid" + o.taxid);
    }
    entity.setSource("eutils");
    entity.setUid(Long.parseLong(o.uid));
    entity.setLastMajorReleaseAccession(o.lastmajorreleaseaccession);
    entity.setFtpPath(o.ftppath_refseq);
    
    return entity;
  }
  
  @Override
  public boolean delete(RefseqGenomeEntity o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public RefseqGenomeEntity convert(Node node) {
    BiodbNode n = new BiodbNode(node);
    String entry = n.getEntry();
    
    RefseqGenomeEntity entity = new RefseqGenomeEntity();
    entity.setEntry(entry);
    return entity;
  }
  
  @Override
  public Long save(RefseqGenomeEntity o) {
    Long id = super.save(o);
    
    if (id != null) {
      Node node = this.service.getNodeById(id);
      Node taxNode = this.service.getEntityNode(o.getTxid(), GlobalLabel.NcbiTaxonomy);
      if (taxNode != null && !Neo4jUtils.isConnected(node, taxNode)) {
        logger.info("[LINK] [{}] -[{}]-> [{}]", taxNode, GenericRelationship.has_taxonomy, node);
        Relationship r = node.createRelationshipTo(taxNode, GenericRelationship.has_taxonomy);
        Neo4jUtils.setCreatedTimestamp(r);
        Neo4jUtils.setUpdatedTimestamp(r);
      }
    }

    return id;
  }

  @Override
  public Map<String, Object> getProperties(RefseqGenomeEntity o) {
    Map<String, Object> properties = null;
    try {
      properties = propertyContainerBuilder.extractProperties(o, RefseqGenomeEntity.class);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return properties;
  }
}
