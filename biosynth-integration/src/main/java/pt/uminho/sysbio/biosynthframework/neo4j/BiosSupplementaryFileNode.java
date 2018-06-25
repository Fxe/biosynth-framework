package pt.uminho.sysbio.biosynthframework.neo4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class BiosSupplementaryFileNode extends BiodbEntityNode {
  //  {major_label=SupplementaryMaterial, entry=21846360/12918_2011_738_MOESM3_ESM.XML, 
  //  file=12918_2011_738_MOESM3_ESM.XML, updated_at=1527566658910, 
  // , created_at=1527029912613, type=sbml, 

  public final String NAME = "file";
  public final String LOCAL_FILE = "local_file";
  public final String PARENT_FILE = "parent_file";
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
  
  public String getName() {
    return (String) this.getProperty(NAME, null);
  }
  
  public String getAbsolutePath() {
    String localFile = (String) this.getProperty(LOCAL_FILE, null);
    return localFile;
  }
  
  public InputStream getInputStream() throws IOException {
    InputStream is = null;

    if (this.hasProperty(PARENT_FILE)) {
//      System.out.println(Neo4jUtils.countLinkType(this));
      Relationship r = this.getSingleRelationship(GenericRelationship.has_file, Direction.BOTH);
      if (r != null) {
        BiosSupplementaryFileNode node = new BiosSupplementaryFileNode(r.getOtherNode(this), null);
        node.getInputStream();
        try (ZipContainer zip = new ZipContainer(node.getAbsolutePath())) {
          for (ZipRecord zr : zip.getInputStreams()) {
            if (this.getName().equals(zr.name)) {
              is = BiosIOUtils.copyToByteArrayStream(zr.is);
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      String localFile = (String) this.getProperty(LOCAL_FILE, null);
      
      if (localFile != null) {
        is = BiosIOUtils.copyToByteArrayStream(new FileInputStream(localFile));
      }
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
