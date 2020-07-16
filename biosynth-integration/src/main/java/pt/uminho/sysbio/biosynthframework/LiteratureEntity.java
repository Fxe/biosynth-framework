package pt.uminho.sysbio.biosynthframework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class LiteratureEntity extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  protected String pubmedEntry;
  protected String doiEntry;
  protected String journal;
  protected String journalAbbreviation;
  protected File folder;
  protected List<SupplementaryMaterialEntity> supplementaryMaterials = new ArrayList<>();
  
  public String getPubmedEntry() { return pubmedEntry;}
  public void setPubmedEntry(String pubmedEntry) { this.pubmedEntry = pubmedEntry;}
  
  public String getDoiEntry() { return doiEntry;}
  public void setDoiEntry(String doiEntry) { this.doiEntry = doiEntry;}
  
  public String getJournal() { return journal;}
  public void setJournal(String journal) { this.journal = journal;}
  
  public String getJournalAbbreviation() { return journalAbbreviation;}
  public void setJournalAbbreviation(String journalAbbreviation) { this.journalAbbreviation = journalAbbreviation;}
  
  public File getFolder() { return folder;}
  public void setFolder(File folder) { this.folder = folder;}
  
  public List<SupplementaryMaterialEntity> getSupplementaryMaterials() {
    return supplementaryMaterials;
  }
  public void setSupplementaryMaterials(List<SupplementaryMaterialEntity> supplementaryMaterials) {
    this.supplementaryMaterials = supplementaryMaterials;
  }
  @Override
  public String toString() {
    return String.format("pmid:%s, %s - %s, %s", pubmedEntry, doiEntry, description, journal);
  }
}
