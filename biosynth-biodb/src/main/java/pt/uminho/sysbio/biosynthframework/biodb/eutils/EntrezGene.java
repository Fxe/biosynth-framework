package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Entrezgene")
public class EntrezGene {

  @XmlElement(name="Entrezgene_track-info")
  public Map<String, Object> Entrezgene_track_info;
  
  public Map<String, Object> Entrezgene_source;
  public Map<String, Object> Entrezgene_gene;
  public Map<String, Object> Entrezgene_prot;
  
  @XmlElement(name="Entrezgene_gene-source")
  public Map<String, Object> Entrezgene_gene_source;
  public Map<String, Object> Entrezgene_locus;
  
  @Override
  public String toString() {
    return super.toString();
  }
}
