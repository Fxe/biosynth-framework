package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName ="TaxaSet")
public class EntrezTaxaSet implements Iterable<EntrezTaxon> {
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName = "Taxon")
  public List<EntrezTaxon> taxons = new ArrayList<> ();

  @Override
  public Iterator<EntrezTaxon> iterator() {
    return taxons.iterator();
  }
  
  @Override
  public String toString() {
    return taxons.toString();
  }
}
