package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.ExternalReference;

public class Neo4jInchiHashBlockReporter extends AbstractNeo4jReporter {

  public Neo4jInchiHashBlockReporter(GraphDatabaseService service) {
    super(service);
  }

  public void report() {
    BMap<String, String> inchiToFikhbMap = new BHashMap<>();
    BMap<String, String> inchiToSikhbMap = new BHashMap<>();
    BMap<String, String> inchiToInchikeyMap = new BHashMap<>();
    Map<String, Set<ExternalReference>> inchiToIds = new HashMap<> ();
    for (Node inchiNode : service.listNodes(MetabolitePropertyLabel.InChI)) {
      String inchi = null;
      String inchikey = null;
      String fikhb = null;
      String sikhb = null;

      inchi = (String) inchiNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
      Relationship inchiToInchikey = inchiNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey, Direction.BOTH);
      if (inchiToInchikey != null) {
        Node inchikeyNode = inchiToInchikey.getOtherNode(inchiNode);
        inchikey = (String) inchikeyNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);

        Node fikhbNode = inchikeyNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey_fikhb, 
            Direction.BOTH).getOtherNode(inchikeyNode);
        Node sikhbNode = inchikeyNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey_sikhb, 
            Direction.BOTH).getOtherNode(inchikeyNode);

        fikhb = (String) fikhbNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
        sikhb = (String) sikhbNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);

        inchiToInchikeyMap.put(inchi, inchikey);
        inchiToFikhbMap.put(inchi, fikhb);
        inchiToSikhbMap.put(inchi, sikhb);
      }

      inchiToIds.put(inchi, new HashSet<ExternalReference> ());
      for (Node cpdNode : Neo4jUtils.collectNodeRelationshipNodes(inchiNode, MetaboliteRelationshipType.has_inchi)) {
        if (cpdNode.hasLabel(GlobalLabel.Metabolite)) {
          inchiToIds.get(inchi).add(new ExternalReference(cpdNode.getProperty("entry").toString(), 
              cpdNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY).toString()));
        }
      }
    }
    //    for (Node fikhbNode : service.listNodes(MetabolitePropertyLabel.FIKHB)) {
    //      for (Node inchiKey)
    //      Node inchikeyNode = fikhbNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey_fikhb, 
    //                                                          Direction.BOTH).getOtherNode(fikhbNode);
    //    }



    StringBuilder sb = new StringBuilder();
    for (String fikhb : inchiToFikhbMap.bkeySet()) {
      for (String i : inchiToFikhbMap.bget(fikhb)) {
        String line = fikhb + "\t" + inchiToSikhbMap.get(i) + "\t" + i + "\t" + StringUtils.join(inchiToIds.get(i), ';');
        sb.append(line).append("\n");
      }
    }

    write(sb, "/inchi_wut.txt");
  }
  
  public static void write(StringBuilder sb, String file) {
    try (OutputStream os = new FileOutputStream(file)) {
      IOUtils.write(sb.toString().getBytes(), os);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
