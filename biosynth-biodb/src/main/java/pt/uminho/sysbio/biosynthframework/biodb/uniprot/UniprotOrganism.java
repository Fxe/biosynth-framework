package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;

@JacksonXmlRootElement(localName = "organism")
public class UniprotOrganism {
  
//  @JacksonXmlElementWrapper(useWrapping=true)
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> name;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  public List<Object> dbReference;
  
  public Long getNCBITaxonomyId() {
    if (dbReference != null) {
      for (Object o : dbReference) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) o;
        String t = JsonMapUtils.getString(map, "type");
        if (t != null && t.equals("NCBI Taxonomy")) {
//          System.out.println(o);
          return Long.parseLong(JsonMapUtils.getString(map, "id")); 
        }
      }
    }
    
    return null;
  }
  
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