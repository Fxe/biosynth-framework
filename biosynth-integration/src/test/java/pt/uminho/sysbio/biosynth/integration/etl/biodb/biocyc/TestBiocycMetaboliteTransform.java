package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.CentralMetaboliteEtlDataCleansing;
import pt.uminho.sysbio.biosynth.integration.etl.EtlDataCleansing;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkWrapper;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.RestBiocycMetaboliteDaoImpl;

public class TestBiocycMetaboliteTransform {
  public static void main(String[] args) {
    RestBiocycMetaboliteDaoImpl dao = new RestBiocycMetaboliteDaoImpl();

    //  RestKeggCompoundMetaboliteDaoImpl dao = new RestKeggCompoundMetaboliteDaoImpl();
    //  dao.setDatabaseVersion("test");
    //  dao.setUseLocalStorage(true);
    //  dao.setSaveLocalStorage(true);
    //  dao.setLocalStorage("/tmp/trash/kegg");
    //  dao.getMetaboliteByEntry("C00001");

    dao.setLocalStorage("D:/var/biodb/biocyc");
    dao.setDatabaseVersion("21.1");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    dao.setPgdb("META");
    //  dao.getMetaboliteByEntry("META:CPD-882");
    //  dao.getMetaboliteByEntry("META:WATER");
//    BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("ACP");
    
//    BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("CPD-4185");
    BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("Cytochromes-c553");
    Map<String, String> map = new HashMap<> ();

    BiocycMetaboliteTransform t = new BiocycMetaboliteTransform(MetaboliteMajorLabel.MetaCyc.toString(), map);
    GraphMetaboliteEntity gcpd = t.apply(cpd);
    System.out.println(cpd.getEntry() + " " + cpd.getName());
    System.out.println("\t" + gcpd.getConnectionTypeCounter());
    for (String l : gcpd.getConnectedEntities().keySet()) {
      int k = 0;
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : gcpd.getConnectedEntities().get(l)) {
        System.out.println("\t" + l + "\t" + p.getLeft().getProperties() + " ==> "+ p);
        k++;
      }
    }
    EtlDataCleansing<GraphMetaboliteEntity> c = new CentralMetaboliteEtlDataCleansing(new CdkWrapper());
    c.etlCleanse(gcpd);
//    try {
//      Map<String, Triple<String, String, EtlCleasingType>> dc = dataCleansing.etlCleanse(cpd);
//      for (String k : dc.keySet()) {
//        System.out.println("\t" + k + "\t" + dc.get(k));
//      }
//    } catch (Exception ee) {
//      ee.printStackTrace();
//    }
  }
}
