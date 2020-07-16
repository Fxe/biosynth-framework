package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class RefseqGenomeEntity extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  protected Long uid;
  
  @MetaProperty
  protected String assemblyName;
  
  @MetaProperty
  protected String lastMajorReleaseAccession;
  
  @MetaProperty
  protected String ftpPath;
  
  @MetaProperty
  protected String biosampleAccession;
  
  @MetaProperty
  protected String biosampleId;
  
  @MetaProperty
  protected String txid;
  
  @MetaProperty
  protected String organism;
  
  @MetaProperty
  protected String refseqCategory;
  
  @MetaProperty
  protected Boolean partialGenomeRepresentation;
  
  @MetaProperty
  protected String submitterOrganization;

  public String getAssemblyName() {
    return assemblyName;
  }

  public void setAssemblyName(String assemblyName) {
    this.assemblyName = assemblyName;
  }

  public String getBiosampleAccession() {
    return biosampleAccession;
  }

  public void setBiosampleAccession(String biosampleAccession) {
    this.biosampleAccession = biosampleAccession;
  }

  public String getBiosampleId() {
    return biosampleId;
  }

  public void setBiosampleId(String biosampleId) {
    this.biosampleId = biosampleId;
  }

  public String getTxid() {
    return txid;
  }

  public void setTxid(String txid) {
    this.txid = txid;
  }

  public String getOrganism() {
    return organism;
  }

  public void setOrganism(String organism) {
    this.organism = organism;
  }

  public String getRefseqCategory() {
    return refseqCategory;
  }

  public void setRefseqCategory(String refseqCategory) {
    this.refseqCategory = refseqCategory;
  }

  public Boolean getPartialGenomeRepresentation() {
    return partialGenomeRepresentation;
  }

  public void setPartialGenomeRepresentation(Boolean partialGenomeRepresentation) {
    this.partialGenomeRepresentation = partialGenomeRepresentation;
  }

  public String getSubmitterOrganization() {
    return submitterOrganization;
  }

  public void setSubmitterOrganization(String submitterOrganization) {
    this.submitterOrganization = submitterOrganization;
  }

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  public String getLastMajorReleaseAccession() {
    return lastMajorReleaseAccession;
  }

  public void setLastMajorReleaseAccession(String lastMajorReleaseAccession) {
    this.lastMajorReleaseAccession = lastMajorReleaseAccession;
  }

  public String getFtpPath() {
    return ftpPath;
  }

  public void setFtpPath(String ftpPath) {
    this.ftpPath = ftpPath;
  }
  
  
}
