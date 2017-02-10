package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Entrezgene")
public class EntrezGene {

  @JacksonXmlRootElement(localName = "Gene-track")
  public static class GeneTrack {
    
//    @JacksonXmlElementWrapper(useWrapping=false)
    @JacksonXmlProperty(localName = "Gene-track_geneid")
    public long Gene_track_geneid;
    
    @JacksonXmlElementWrapper(useWrapping=false)
    @JacksonXmlProperty(localName="Gene-track_create-date")
    public Map<String, Object> Gene_track_create_date;
  }
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName = "Entrezgene_track-info")
  public Map<String, Object> Entrezgene_track_info;
  
//  @JacksonXmlElementWrapper(useWrapping=false)
//  @XmlElement(name="Entrezgene_track-info")
//  public Map<String, Object> Entrezgene_track_info;
  
  public Map<String, Object> Entrezgene_source;
  public Map<String, Object> Entrezgene_gene;
  public Map<String, Object> Entrezgene_prot;
  
  public List<Map<String, Object>> Entrezgene_comments;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName="Entrezgene_gene-source")
  public Map<String, Object> Entrezgene_gene_source;
  public List<Map<String, Object>> Entrezgene_locus;
  
  @JacksonXmlElementWrapper(useWrapping=true)
  @JacksonXmlProperty(localName="Entrezgene_xtra-iq")
  public List<Map<String, Object>> Entrezgene_xtra_iq;
  
  @JacksonXmlElementWrapper(useWrapping=true)
  @JacksonXmlProperty(localName="Entrezgene_non-unique-keys")
  public List<Map<String, Object>> Entrezgene_non_unique_keys;
  
  @Override
  public String toString() {
    return super.toString();
  }
}
