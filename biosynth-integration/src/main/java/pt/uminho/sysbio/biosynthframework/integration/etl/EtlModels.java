package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.io.IOException;
import java.io.InputStream;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlStreamSbmlReader;

public class EtlModels extends AbstractNeo4jEtl {
  
  public EtlModels(GraphDatabaseService service) {
    super(service);
  }
  
  public void etlXml(InputStream is, String entry) {
    try {
      XmlStreamSbmlReader reader = new XmlStreamSbmlReader(is);
      XmlSbmlModel xmodel = reader.parse();
      TheStaticModelLoader.loadModel(xmodel, entry, service);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void etl(String e) {
    // TODO Auto-generated method stub
    
  }
}
