package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class EntrezProteinSet implements Iterable<EntrezProtein> {
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName = "GBSet")
  public List<EntrezProtein> proteins = new ArrayList<> ();

  @Override
  public Iterator<EntrezProtein> iterator() {
    return proteins.iterator();
  }
  
  @Override
  public String toString() {
    return proteins.toString();
  }
}
