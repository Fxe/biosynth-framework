package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class BiocycXmlQueryResult {
  @JsonProperty("ptools-version")
  public Object ptools_version;
  
//  @JsonProperty("base")
  public Object base;
  
//  @JsonProperty("metadata")
  public Map<String, Object> metadata;
  
//  @JacksonXmlElementWrapper(useWrapping = false)
//  public List<Map<String, Object>> Gene = new ArrayList<> ();
  
  private List<Map<String, Object>> Protein = new ArrayList<> ();
  
  
  public List<Map<String, Object>> getProtein() {
    System.out.println("foo");
    return Protein;
  }
  
  @JacksonXmlProperty(localName = "Protein")
  public void setProtein(Map<String, Object> protein) {
    Protein.add(protein);
  }
  
  @JacksonXmlProperty(localName = "Gene")
  public void setGene(Map<String, Object> gene) {
    Protein.add(gene);
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  public List<Map<String, Object>> Error  = new ArrayList<> ();
  
}
