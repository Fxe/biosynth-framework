package pt.uminho.sysbio.biosynth.integration.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uminho.biosynth.core.data.integration.chimera.service.ConflictDecision;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.SplitStrategy;
import pt.uminho.sysbio.biosynth.integration.BFS;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jSuperDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

@Service
@Transactional(readOnly=true, value="neo4jMetaTransactionManager")
public class DefaultMetaboliteIntegrationServiceImpl<M extends Metabolite> 
extends BasicIntegrationService
implements MetaboliteIntegrationService {

  private static final Logger logger = LoggerFactory.getLogger(DefaultMetaboliteIntegrationServiceImpl.class);

  private MetaboliteHeterogeneousDao<M> data;

  public MetaboliteHeterogeneousDao<M> getData() { return data;}
  public void setData(MetaboliteHeterogeneousDao<M> data) { this.data = data;}

  private Map<Long, Map<Long, Set<Long>>> ctrToMembers = new HashMap<> ();
  private Map<Long, Map<Long, Long>>      membersToCtr = new HashMap<> ();

  @Autowired
  private Neo4jSuperDao neo4jMetaDao;

  @Autowired
  public DefaultMetaboliteIntegrationServiceImpl(
      MetaboliteHeterogeneousDao<M> data, 
      IntegrationMetadataDao meta,
      Neo4jSuperDao neo4jMetaDao) {
    super(meta);
    this.data = data;
    this.neo4jMetaDao = neo4jMetaDao;
  }

  @Override
  public Map<Set<String>, Integer> countNodesByLabelSet() {
    Map<Set<String>, Integer> result = new HashMap<> (); 
    Map<Set<String>, Set<Long>> output = neo4jMetaDao.superHeavyMethod();
    for (Set<String> key : output.keySet()) {
      result.put(key, output.get(key).size());
    }

    return result;
  }

  @Override
  public List<IntegratedCluster> pageClusters(Long iid, int firstResult,
      int maxResults) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public int countIntegratedClustersByIntegrationId(Long iid) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public List<IntegratedCluster> generateIntegratedClusters(Long iid,
      ClusteringStrategy clusteringStrategy, Set<Long> initial,
      Set<Long> domain, ConflictDecision conflictDecision) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IntegratedCluster createCluster(IntegrationSet integrationSet,
      String name, Set<Long> members, String description,
      ConflictDecision conflictDecision) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public List<IntegratedCluster> createCluster(IntegrationSet integrationSet,
      ClusteringStrategy clusteringStrategy, Set<Long> initial,
      Set<Long> domain, ConflictDecision conflictDecision, Long limit) {

    if (limit == null) limit = Long.MAX_VALUE;
    int i = 0;
    List<IntegratedCluster> integratedClusters = new ArrayList<> ();

    //Generate Clusters
    List<Set<Long>> generatedClusters = this.generateClusters(clusteringStrategy, initial, domain);
    logger.info(String.format("Generated an initial of %d clusters.", generatedClusters.size()));
    List<Set<Long>> uniqueMembershipClusters = this.resolveMembershipConflict(generatedClusters);
    logger.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));
    //		Map<Long, Set<Long>> prevClusters = new HashMap<> ();
    //		for (IntegratedCluster integratedCluster : this.meta.getAllIntegratedClusters(integrationSet.getId())) {
    //			Long cid = integratedCluster.getId();
    //			Set<Long> clustersElements = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
    //			prevClusters.put(cid, clustersElements);
    //		}
    //		LOGGER.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));

    return integratedClusters;
  }
  @Override
  public List<IntegratedCluster> splitCluster(IntegrationSet integrationSet,
      IntegratedCluster integratedCluster, SplitStrategy splitStrategy,
      String name, String description) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public IntegratedCluster updateCluster(IntegratedCluster integratedCluster,
      String name, Set<Long> members, String description) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public IntegratedCluster mergeCluster(IntegrationSet integrationSet,
      Set<Long> cidList, String name, Set<Long> members,
      String description) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public void deleteCluster(IntegrationSet integrationSet, Long cid) {
    // TODO Auto-generated method stub

  }
  @Override
  public List<Long> listAllIntegratedCompounds(IntegrationSet integrationSet) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public List<Long> listAllUnintegratedCompounds(IntegrationSet integrationSet) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public IntegratedCluster createCluster(String query) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public IntegratedCluster createCluster(IntegrationSet integrationSet,
      ClusteringStrategy strategy) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public IntegratedCluster mergeCluster(String query) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public IntegratedCluster mergeCluster(ClusteringStrategy strategy) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public IntegratedCluster mergeCluster(String name, Set<Long> elements,
      String description) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public Map<Long, IntegratedCluster> splitCluster(Long cid, Set<Long> keep,
      String entry, String description) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public void updateCluster(Long cid, String entry, String description,
      Set<Long> elements) {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public IntegrationSet getCurrentIntegrationSet() {
    throw new RuntimeException("DEPRECATED");
  }
  @Override
  public Map<String, Integer> getDataStatistics() {
    // TODO Auto-generated method stub
    return null;
  }

  private List<Set<Long>> generateClusters(
      ClusteringStrategy clusteringStrategy,
      Set<Long> initial, Set<Long> domain) {

    List<Set<Long>> generatedClusters = new ArrayList<> ();

    Set<Long> visitedIds = new HashSet<> ();
    for (Long i : initial) {
      if (!visitedIds.contains(i)) {
        clusteringStrategy.setInitialNode(i);
        Set<Long> clusterElements = clusteringStrategy.execute();
        logger.debug(String.format("%s generated %d members from %d", clusteringStrategy.getClass().getSimpleName(), clusterElements.size(), i));
        clusterElements.retainAll(domain);
        if (!clusterElements.isEmpty()) {
          visitedIds.addAll(clusterElements);
          generatedClusters.add(clusterElements);
        }
      }
    }

    return generatedClusters;
  }

  private List<Set<Long>> resolveMembershipConflict(List<Set<Long>> clusterList) {
    List<Set<Long>> uniqueMembershipClusters = new ArrayList<> ();

    UndirectedGraph<Long, Integer> graph = new UndirectedSparseGraph<>();  
    Integer counter = 0;
    Set<Long> eids = new HashSet<> ();
    for (Set<Long> cluster : clusterList) {
      Long prev = null;
      if (cluster.size() > 1) {
        for (Long eid : cluster) {
          eids.add(eid);
          if (prev != null) {
            graph.addEdge(counter++, prev, eid);
            //						DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
            //						graph.addEdge(edge);
          }
          prev = eid;
        }
      } else {
        for (Long eid : cluster) {
          eids.add(eid);
          graph.addVertex(eid);
        }
      }
    }

    Set<Long> eidsProcessed = new HashSet<> ();
    for (Long eid : eids) {
      if (!eidsProcessed.contains(eid)) {

        Set<Long> cluster = BFS.run(graph, eid);
        eidsProcessed.addAll(cluster);
        uniqueMembershipClusters.add(cluster);
      }
    }

    return uniqueMembershipClusters;
  }

  @Override
  public List<IntegratedCluster> getAllMetaboliteIntegratedClusterEntries(Long iid) {
    Set<Long> ids = meta.getAllIntegratedClusterIdsByType(iid, IntegrationNodeLabel.MetaboliteCluster.toString());

    logger.debug(String.format("Found %d clusters", ids.size()));

    List<IntegratedCluster> integratedClusters = new ArrayList<> ();

    for (Long id : ids) {
      IntegratedCluster integratedCluster = meta.getIntegratedClusterById(id);
      if (integratedCluster != null) {
        integratedClusters.add(integratedCluster);
      } else {
        logger.error(String.format("Unable to retrieve cluster %d", id));
      }
    }
    return integratedClusters;
  }

  @Override
  public List<IntegratedCluster> getAllReactionIntegratedClusterEntries(Long iid) {
    Set<Long> ids = meta.getAllIntegratedClusterIdsByType(iid, IntegrationNodeLabel.ReactionCluster.toString());

    logger.debug(String.format("Found %d clusters", ids.size()));

    List<IntegratedCluster> integratedClusters = new ArrayList<> ();

    for (Long id : ids) {
      IntegratedCluster integratedCluster = meta.getIntegratedClusterById(id);
      if (integratedCluster != null) {
        integratedClusters.add(integratedCluster);
      } else {
        logger.error(String.format("Unable to retrieve cluster %d", id));
      }
    }
    return integratedClusters;
  }

  @Override
  public void lalal(String type, long iid, int page, int limit) {
    //		meta.get
  }

  @Override
  public Map<Long, Long> getMetaboliteUnificationMap(long iid) {
    return this.meta.getUnificationMapping(
        iid, 
        IntegrationNodeLabel.MetaboliteCluster.toString(), 
        IntegrationNodeLabel.MetaboliteMember.toString());
  }

  @Override
  public IntegratedMember getIntegratedMemberByReferenceEid(long referenceEid) {
    return this.meta.getIntegratedMemberByReferenceEid(referenceEid);
  }

  @Override
  public IntegratedMember getIntegratedMemberById(long id) {
    return this.meta.getIntegratedMemberById(id);
  }


  @Transactional(readOnly=false, value="neo4jMetaTransactionManager")
  @Override
  public Pair<Set<Long>, Set<Long>> remapIntegrationSet(long itgId) {
    //    IntegrationSet itmeta.getIntegrationSet(itgId);
    GraphDatabaseService service = neo4jMetaDao.getGraphDatabaseService();
    Node itgNode = service.getNodeById(itgId);
    Set<Long> integratedMembers = new HashSet<> ();
    for (Node eidNode : Neo4jUtils.collectNodeRelationshipNodes(
        itgNode, IntegrationRelationshipType.has_integrated_metabolite)) {
      integratedMembers.add(
          (long)eidNode.getProperty(Neo4jDefinitions.MEMBER_REFERENCE));
    }

    Set<Long> addedMembers = new HashSet<> ();
    for (Node ctrNode : Neo4jUtils.collectNodeRelationshipNodes(
        itgNode, IntegrationRelationshipType.IntegratedMetaboliteCluster)) {
      for (Node eidNode : Neo4jUtils.collectNodeRelationshipNodes(
          ctrNode, IntegrationRelationshipType.Integrates)) {
        long cpdId = (long)eidNode.getProperty(Neo4jDefinitions.MEMBER_REFERENCE);
        if (!integratedMembers.contains(cpdId)) {
          itgNode.createRelationshipTo(eidNode, 
              IntegrationRelationshipType.has_integrated_metabolite);
          addedMembers.add(cpdId);
        }
      }
    }

    return new ImmutablePair<Set<Long>, Set<Long>>(integratedMembers, 
        addedMembers);
  }

  public void storeUnificationMapToCache(long itgId) {
    logger.info("Cache integration set {}", itgId);
    Map<Long, Long> metaboliteUnificationMap = 
        meta.getUnificationMapping(
            itgId, IntegrationNodeLabel.MetaboliteCluster.toString(), 
            IntegrationNodeLabel.MetaboliteMember.toString());
    this.membersToCtr.put(itgId, metaboliteUnificationMap);
    this.ctrToMembers.put(itgId, 
        CollectionUtils.reverseMap(metaboliteUnificationMap));
  }

  public int getCacheMemorySize() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this.membersToCtr);
      oos.writeObject(this.ctrToMembers);
      oos.close();
      return baos.size();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return -1;
  }

  @Override
  public Map<Long, Set<Long>> sortMembers(Set<Long> ids, long itgId) {
    IntegrationSet itg = this.meta.getIntegrationSet(itgId);
    if (itg == null) {
      return null;
    }

    if (!this.ctrToMembers.containsKey(itgId)) {
      storeUnificationMapToCache(itgId);
    }


    Map<Long, Set<Long>> result = new HashMap<> ();
    Set<Long> visited = new HashSet<> ();
    for (long id : ids) {
      if (!visited.contains(id)) {
        if (membersToCtr.get(itgId).containsKey(id)) {
          //get cluster id from integration set
          long ctrId = membersToCtr.get(itgId).get(id);
          //find all members that share same cluster
          Set<Long> grouped = Sets.intersection(
              ctrToMembers.get(itgId).get(ctrId), ids);
          result.put(ctrId, grouped);
          visited.addAll(grouped);
        } else {
          if (!result.containsKey(-1L)) {
            result.put(-1L, new HashSet<Long> ());
          }
          result.get(-1L).add(id);
          visited.add(id);
        }
      }
    }
    // TODO Auto-generated method stub
    return result;
  }
}
