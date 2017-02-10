package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Joiner;

@JacksonXmlRootElement(localName = "organism")
public class UniprotOrganism {
  
//  @JacksonXmlElementWrapper(useWrapping=true)
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> name;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> dbReference;
  
  @Override
  public String toString() {
    Map<String, Object> wut = new HashMap<> ();
    if (name != null)
    wut.put("name", name);
    if (dbReference != null)
    wut.put("dbReference", dbReference);
//    if (lineage != null)
//    wut.put("lineage", lineage);
    return Joiner.on('\n').withKeyValueSeparator("\t").join(wut);
  }
}