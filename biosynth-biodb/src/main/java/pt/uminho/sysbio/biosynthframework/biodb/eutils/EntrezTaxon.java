package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName ="Taxon")
public class EntrezTaxon {
  
  public long TaxId;
  public String ScientificName;
  public long ParentTaxId;
  public String rank;
  public String Division;
  public String Lineage;
  public String CreateDate;
  public String UpdateDate;
  public String PubDate;
  
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return String.format("[%d]%s", TaxId, ScientificName);
  }
}
