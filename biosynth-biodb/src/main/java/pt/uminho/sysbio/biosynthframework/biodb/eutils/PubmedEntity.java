package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class PubmedEntity extends AbstractBiosynthEntity {
  
  private static final long serialVersionUID = 1L;
  
  protected String title;
  protected String journal;
  protected String journalAbbreviation;
  protected String doi;
  
  public String getTitle() { return title;}
  public void setTitle(String title) { this.title = title;}
  public String getJournal() { return journal;}
  public void setJournal(String journal) { this.journal = journal;}
  public String getJournalAbbreviation() { return journalAbbreviation;}
  public void setJournalAbbreviation(String journalAbbreviation) { this.journalAbbreviation = journalAbbreviation;}
  public String getDoi() { return doi;}
  public void setDoi(String doi) { this.doi = doi;}
  
  @Override
  public String toString() {
    return String.format("Pubmed[%s]", entry);
  }
}
