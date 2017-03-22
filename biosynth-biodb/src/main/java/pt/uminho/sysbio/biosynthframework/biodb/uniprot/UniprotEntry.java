package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;

@JacksonXmlRootElement(localName ="entry")
public class UniprotEntry {
  
//  @JacksonXmlProperty(isAttribute=true)
  public String dataset;
//  @JacksonXmlProperty(isAttribute=true)
  public String created;
//  @JacksonXmlProperty(isAttribute=true)
  public String modified;
//  @JacksonXmlProperty(isAttribute=true)
  public String version;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<String> accession;
  
  public String name;
  
  public Object protein;
  
  
  public Map<String, Object> gene;
  
//  @JacksonXmlElementWrapper(useWrapping=false)
  public UniprotOrganism organism;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> reference;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> comment;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> dbReference;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> feature;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> evidence;
  
  public UniprotSequence sequence;
  
  public String getLocus() {
    if (gene != null && gene.containsKey("name")) {
      return JsonMapUtils.getString(gene, "name", "");
    }
    return null;
  }
  
  @Override
  public String toString() {
    Map<String, Object> wut = new HashMap<> ();
    if (dataset != null)
    wut.put("dataset", dataset);
    if (created != null)
    wut.put("created", created);
    if (modified != null)
    wut.put("modified", modified);
    if (version != null)
    wut.put("version", version);
    if (name != null)
    wut.put("name", name);
    if (accession != null)
    wut.put("accession", accession);
    if (protein != null)
    wut.put("protein", protein);
    if (gene != null)
    wut.put("gene", gene);
    if (organism != null)
    wut.put("organism", organism);
    if (reference != null)
    wut.put("reference", reference);
    if (comment != null)
    wut.put("comment", comment);
    if (dbReference != null)
    wut.put("dbReference", dbReference);
    if (sequence != null)
    wut.put("sequence", sequence);
    if (evidence != null)
    wut.put("evidence", evidence);
    if (feature != null)
    wut.put("feature", feature);
    return Joiner.on("\n--\n").withKeyValueSeparator(":\t").join(wut);
  }
}
