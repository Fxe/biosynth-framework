package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName ="uniprot")
public class UniprotResult {

  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName = "entry")
  public List<UniprotEntry> entries = new ArrayList<> ();
  
  public String copyright;
}
