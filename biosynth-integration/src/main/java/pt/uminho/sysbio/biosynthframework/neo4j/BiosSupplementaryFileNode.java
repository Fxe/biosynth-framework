package pt.uminho.sysbio.biosynthframework.neo4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosSupplementaryFileNode extends BiodbEntityNode {
  //  {major_label=SupplementaryMaterial, entry=21846360/12918_2011_738_MOESM3_ESM.XML, 
  //  file=12918_2011_738_MOESM3_ESM.XML, updated_at=1527566658910, 
  // , created_at=1527029912613, type=sbml, 

  public final String LOCAL_FILE = "local_file";
  public final String URL = "url";
  public final String MD5 = "md5";
  
  public BiosSupplementaryFileNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public String getMD5() {
    return (String) this.getProperty(MD5, null);
  }
  
  public Long getSize() {
    return (Long) this.getProperty("size", null);
  }
  
  public InputStream getInputStream() throws IOException {
    InputStream is = null;
    
    String localFile = (String) this.getProperty(LOCAL_FILE, null);
    
    if (localFile != null) {
      is = new FileInputStream(localFile);
    }
    
    return is;
  }
  
  public URL getUrl() throws MalformedURLException {
    URL url = null;
    
    String urlString = (String) this.getProperty(URL, null);
    
    if (urlString != null) {
      url = new URL(urlString);
    }
    
    return url;
  }
}
