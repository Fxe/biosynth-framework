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
  
  public Long etlXml(InputStream is, String entry) {
    Long modelId = null;
    try {
      XmlStreamSbmlReader reader = new XmlStreamSbmlReader(is);
      XmlSbmlModel xmodel = reader.parse();
      modelId = TheStaticModelLoader.loadModel(xmodel, entry, service);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return modelId;
  }

  @Override
  public void etl(String e) {
    // TODO Auto-generated method stub
  }
}
