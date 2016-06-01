package pt.uminho.sysbio.biosynth.integration.curation;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.uminho.sysbio.biosynth.integration.AnnotationType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jSuperDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

@Service
@Transactional(readOnly=true, value="neo4jDataTransactionManager")
public class Neo4jAnnotationServiceImpl implements AnnotationService {
  
  private Neo4jSuperDao neo4jDataDao;
  
  @Autowired
  public Neo4jAnnotationServiceImpl(Neo4jSuperDao neo4jDataDao) {
    this.neo4jDataDao = neo4jDataDao;
  }
  
  @Override
  public Long addAnnotation(AnnotationType type, long src, long dst, String user) {
    GraphDatabaseService service = neo4jDataDao.getGraphDatabaseService();
    Node srcNode = service.getNodeById(src);
    Node dstNode = service.getNodeById(dst);
    Relationship r = srcNode.createRelationshipTo(
        dstNode, MetabolicModelRelationshipType.has_crossreference_to);
    Neo4jUtils.setCreatedTimestamp(r);
    Neo4jUtils.setUpdatedTimestamp(r);
    r.setProperty("type", type.toString());
    r.setProperty("user", user);
    
    return r.getId();
  }
  
  @Override
  public Long deleteAnnotation(long src, long dst) {
    GraphDatabaseService service = neo4jDataDao.getGraphDatabaseService();
    Node srcNode = service.getNodeById(src);
    Node dstNode = service.getNodeById(dst);
    Relationship r = Neo4jUtils.getRelationshipBetween(
        srcNode, dstNode, Direction.BOTH);
    
    if (r == null) {
      return null;
    }
    
    r.delete();
    
    return r.getId();
  }
}
