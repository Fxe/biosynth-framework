package pt.uminho.sysbio.biosynthframework.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;

import edu.uminho.biosynth.core.data.integration.etl.staging.transform.KeggMetaboliteStagingTransform;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.CentralMetaboliteEtlDataCleansing;
import pt.uminho.sysbio.biosynth.integration.etl.DefaultMetaboliteEtlExtract;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.HbmNeo4jHybridMetaboliteEtlPipeline;
import pt.uminho.sysbio.biosynth.integration.etl.HeterogenousMetaboliteEtlLoad;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.LipidmapsMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg.KeggCompoundTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkWrapper;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.SdfLipidmapsMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosVersionNode;

public class TTTest {
  
  public static boolean compare(KeggCompoundMetaboliteEntity cpd1, KeggCompoundMetaboliteEntity cpd2) {
    if (cpd1 != null && cpd2 != null) {
      String n1 = cpd1.getName();
      String n2 = cpd2.getName();
      if (!n1.equals(n2)) {
        System.out.println(n1);
        System.out.println(n2);
        return false;
      }
      
      return true;
    }
    return true;
  }
  
  public static boolean compare(GraphMetaboliteEntity cpd1, GraphMetaboliteEntity cpd2) {
    if (cpd1 != null && cpd2 != null) {
      String n1 = cpd1.getName();
      String n2 = cpd2.getName();
      if (!n1.equals(n2)) {
        System.out.println(n1);
        System.out.println(n2);
        return false;
      }
      
      return true;
    }
    return true;
  }
  
  public static void awsomeTest() {

//  //META:RXN-13833
//  //META:M-CRESOL-METHYLCATECHOL-RXN
//  NotFoundReporter reporter = new NotFoundReporter(service);
//  reporter.aaa2();
//  
    
    RestKeggCompoundMetaboliteDaoImpl dao1 = new RestKeggCompoundMetaboliteDaoImpl();
    dao1.setDatabaseVersion("84.0");
    dao1.setLocalStorage("D:\\var\\biodb\\kegg");
    dao1.setUseLocalStorage(true);
    dao1.setSaveLocalStorage(true);
    RestKeggCompoundMetaboliteDaoImpl dao2 = new RestKeggCompoundMetaboliteDaoImpl();
    dao2.setDatabaseVersion("86.0");
    dao2.setLocalStorage("D:\\var\\biodb\\kegg");
    dao2.setUseLocalStorage(true);
    dao2.setSaveLocalStorage(true);
    
    KeggCompoundTransform transform = new KeggCompoundTransform();
    
//    for (String e2 : dao2.getAllMetaboliteEntries()) {
      String e2 = "C00288";
      KeggCompoundMetaboliteEntity cpd1 = dao1.getMetaboliteByEntry(e2);
      System.out.println();
      KeggCompoundMetaboliteEntity cpd2 = dao2.getMetaboliteByEntry(e2);
//      if (cpd1 != null && cpd2 != null) {
        GraphMetaboliteEntity gcpd1 = transform.etlTransform(cpd1);
        GraphMetaboliteEntity gcpd2 = transform.etlTransform(cpd2);
        if (!compare(gcpd1, gcpd2)) {
          System.out.println(e2);
//          break;
        }
//      }

//    }
      
     System.out.println(gcpd1.getVersion());
     System.out.println(gcpd2.getVersion());
    BiodbGraphDatabaseService service = 
        TestConfiguration.getTestGraphDatabaseService("/tmp/biodb/bios.test");
    Neo4jGraphMetaboliteDaoImpl gdao = new Neo4jGraphMetaboliteDaoImpl(service);
////    HbmNeo4jHybridMetaboliteEtlPipeline
    Transaction tx = service.beginTx();
    gcpd1 = gdao.saveMetabolite(null, gcpd1);
    long cpdId1 = gcpd1.getId();
    
    
    
    gcpd2 = gdao.saveMetabolite(null, gcpd2);
    Long cpdId2 = gcpd2.getId();
    
    Node cpdNode1 = service.getNodeById(cpdId1);
    BiodbMetaboliteNode cpdNode2 = service.getMetabolite(cpdId2);
    
    System.out.println(Neo4jUtils.getLabels(cpdNode1) + "\t" + cpdNode1.getAllProperties());
    System.out.println(Neo4jUtils.getLabels(cpdNode2) + "\t" + cpdNode2.getAllProperties());
    

    
    tx.failure();
    tx.close();
    
    service.shutdown();
  }
  
  public static<SRC extends Metabolite> void dieETL(GraphDatabaseService service, 
      EtlTransform<SRC, GraphMetaboliteEntity> transform, MetaboliteDao<SRC> dao1, String cpdEntry) {
    Neo4jGraphMetaboliteDaoImpl dst = new Neo4jGraphMetaboliteDaoImpl(service);
    HbmNeo4jHybridMetaboliteEtlPipeline<SRC, GraphMetaboliteEntity> etl
      = new HbmNeo4jHybridMetaboliteEtlPipeline<>();
    etl.setSkipLoad(false);
    etl.setGraphDatabaseService(service);
    etl.setSessionFactory(null);
    etl.setEtlDataCleasingSubsystem(new CentralMetaboliteEtlDataCleansing(new CdkWrapper()));
    etl.setExtractSubsystem(new DefaultMetaboliteEtlExtract<SRC>(dao1));
    etl.setLoadSubsystem(new HeterogenousMetaboliteEtlLoad<GraphMetaboliteEntity>(dst));
    etl.setTransformSubsystem(transform);
    
    etl.etl(cpdEntry);
  }
  
  
  public static void aaaa(BiodbGraphDatabaseService service, MetaboliteMajorLabel database) {
    Map<String, Set<Long>> aa = new HashMap<>();
    for (BiodbMetaboliteNode cpd : service.listMetabolites(database)) {
      String version = cpd.getVersion();
      Map<String, Object> m = new HashMap<>(cpd.getAllProperties());
      m.remove("mol");
      System.out.println("[" + version + "]\t"+ m);
      BiosVersionNode vnode = cpd.getPreviousVersion();
      if (vnode != null) {
        version = vnode.getVersion();
        m = new HashMap<>(vnode.getAllProperties());
        m.remove("mol");
        System.out.println("[" + version + "]\t"+ m);
        while ((vnode = vnode.getPreviousVersion()) != null) {
          version = vnode.getVersion();
          m = new HashMap<>(vnode.getAllProperties());
          m.remove("mol");
          System.out.println("[" + version + "]\t"+ m);
        }
      }
      if (!aa.containsKey(version)) {
        aa.put(version, new HashSet<Long>());
      }
      aa.get(version).add(cpd.getId());
    }
    
    for (String v : aa.keySet()) {
      System.out.println(v + " " + aa.get(v).size());
    }
  }
  
  public static void main(String[] args) {
    RestKeggCompoundMetaboliteDaoImpl dao1 = new RestKeggCompoundMetaboliteDaoImpl();
    dao1.setDatabaseVersion("2015");
    dao1.setLocalStorage("D:\\var\\biodb\\kegg");
    dao1.setSaveLocalStorage(true);
    dao1.setUseLocalStorage(true);
//    dao1.getByEntry(e)
//    RestKeggDrugMetaboliteDaoImpl dao2 = new RestKeggDrugMetaboliteDaoImpl();
////    dao2.getByEntry(entry)
//    RestKeggGlycanMetaboliteDaoImpl dao3 = new RestKeggGlycanMetaboliteDaoImpl();
////    dao3.getByEntry(entry)
//    RestKeggReactionDaoImpl dao4 = new RestKeggReactionDaoImpl();
////    dao4.getByEntry(entry)
//    RestBiocycMetaboliteDaoImpl dao5 = new RestBiocycMetaboliteDaoImpl();
////    dao4.getMetaboliteByEntry(entry)
//    RestBiocycReactionDaoImpl dao6 = new RestBiocycReactionDaoImpl();
////    dao6.getReactionByEntry(entry)
//    InternalBigg1MetaboliteDaoImpl dao7 = new InternalBigg1MetaboliteDaoImpl();
////    dao7.getByEntry(entry)
//    InternalBigg1ReactionDaoImpl dao8 = new InternalBigg1ReactionDaoImpl();
////    dao8.getByEntry(entry)
//    RestBiggMetaboliteDaoImpl dao9 = new RestBiggMetaboliteDaoImpl("", "");
////    dao9.getByEntry(entry)
//    RestBiggReactionDaoImpl dao10 = new RestBiggReactionDaoImpl("", "");
////    dao10.getByEntry(entry)
//    GithubModelSeedMetaboliteDaoImpl dao11 = new GithubModelSeedMetaboliteDaoImpl("");
////    dao11.getMetaboliteByEntry(entry)
//    Object dao12 = null;
//    
//    XmlHmdbMetaboliteDaoImpl dao13 = new XmlHmdbMetaboliteDaoImpl("", "");
////    dao13.getMetaboliteByEntry(entry)
//    SdfLipidmapsMetaboliteDaoImpl dao14 = new SdfLipidmapsMetaboliteDaoImpl(new File(""), "");
//    dao14.getMetaboliteByEntry(entry)
    
//    System.out.println(dao1.getAllEntries().size());
//    for (String e : dao1.getAllMetaboliteEntries()) {
//      KeggCompoundMetaboliteEntity cpd = dao1.getMetaboliteByEntry(e);
//      System.out.println(cpd.getEntry() + " " + cpd.getFormula() + " " + cpd.getName());
//    }
    
//    KeggCompoundTransform transform = new KeggCompoundTransform();
//    KeggCompoundMetaboliteEntity e1 = dao1.getByEntry("C00001");
//    GraphMetaboliteEntity gcpd = transform.apply(e1);
    
//    System.out.println(gcpd.getProperties());
//    for (String k : gcpd.getConnectedEntities().keySet()) {
//      for (Pair<?, AbstractGraphNodeEntity> p : gcpd.getConnectedEntities().get(k)) {
//        System.out.println(k + p.getRight().getProperties());
//      }
//    }
    
//    RestKeggCompoundMetaboliteDaoImpl daoV1 = new RestKeggCompoundMetaboliteDaoImpl();
//    daoV1.setDatabaseVersion("2015");
//    daoV1.setLocalStorage("D:\\var\\biodb\\kegg");
//    daoV1.setSaveLocalStorage(true);
//    daoV1.setUseLocalStorage(true);
//    RestKeggCompoundMetaboliteDaoImpl daoV2 = new RestKeggCompoundMetaboliteDaoImpl();
//    daoV2.setDatabaseVersion("84.0");
//    daoV2.setLocalStorage("D:\\var\\biodb\\kegg");
//    daoV2.setSaveLocalStorage(true);
//    daoV2.setUseLocalStorage(true);
//    RestKeggCompoundMetaboliteDaoImpl daoV3 = new RestKeggCompoundMetaboliteDaoImpl();
//    daoV3.setDatabaseVersion("86.0");
//    daoV3.setLocalStorage("D:\\var\\biodb\\kegg");
//    daoV3.setSaveLocalStorage(true);
//    daoV3.setUseLocalStorage(true);
//    String cpdEntry = "C15812";

    SdfLipidmapsMetaboliteDaoImpl daoV1 = new SdfLipidmapsMetaboliteDaoImpl(
        new File("D:\\var\\biodb\\lipidmaps/LMSDFDownload28Jun15.zip"), "28Jun15");
    SdfLipidmapsMetaboliteDaoImpl daoV2 = new SdfLipidmapsMetaboliteDaoImpl(
        new File("D:\\var\\biodb\\lipidmaps/LMSDFDownload12Dec17.zip"), "12Dec17");
    LipidmapsMetaboliteTransform transform = new LipidmapsMetaboliteTransform();
    String cpdEntry = "LMFA11000147";
    
    
    BiodbGraphDatabaseService service = TestConfiguration.getTestGraphDatabaseService("D:\\tmp\\biodb\\kegg_vtest");
    Transaction tx = service.beginTx();
//    for (ConstraintDefinition a : service.schema().getConstraints()) {
//      System.out.println(a.getLabel() + " " + a.getPropertyKeys() + " " + a.getConstraintType());
//    }
//    
//    for (BiodbMetaboliteNode cpdNode : service.listMetabolites(MetaboliteMajorLabel.LigandCompound)) {
//      System.out.println(cpdNode.getAllProperties());
//    }
    dieETL(service, transform, daoV1, cpdEntry);
    dieETL(service, transform, daoV2, cpdEntry);
//    dieETL(service, transform, daoV3, cpdEntry);
    aaaa(service, MetaboliteMajorLabel.LipidMAPS);
//    etl.etl("C00001");
    
    tx.failure();
    tx.close();
    
    service.shutdown();
  }
}