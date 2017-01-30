package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName ="Entrezgene-Set")
public class EntrezGeneSet implements Iterable<EntrezGene> {
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName = "Entrezgene")
  public List<EntrezGene> genes = new ArrayList<> ();

  @Override
  public Iterator<EntrezGene> iterator() {
    return genes.iterator();
  }
  
  @Override
  public String toString() {
    return genes.toString();
  }
}
