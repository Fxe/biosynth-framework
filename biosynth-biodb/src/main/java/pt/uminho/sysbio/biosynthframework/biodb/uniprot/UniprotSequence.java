package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.common.base.Joiner;

public class UniprotSequence {
  public String checksum;
  public String modified;
  public Double mass;
  public Integer length;
  public Boolean precursor;
  
  @JacksonXmlText
  public String sequence;
  
  @Override
  public String toString() {
    Map<String, Object> wut = new HashMap<> ();
    if (checksum != null)
    wut.put("checksum", checksum);
    if (modified != null)
    wut.put("modified", modified);
    if (mass != null)
    wut.put("mass", mass);
    if (length != null)
    wut.put("length", length);
    if (precursor != null)
    wut.put("precursor", precursor);
    if (sequence != null)
    wut.put("sequence", sequence);
    return Joiner.on('\n').withKeyValueSeparator("\t").join(wut);
  }
}
