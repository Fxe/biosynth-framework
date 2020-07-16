package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class EutilsAssemblyObject extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  public String uid;
  public String rsuid;
  public String gbuid;
  public String assemblyaccession;
  public String lastmajorreleaseaccession;
  
  public String latestaccession;
  public String assemblyname;
  public String chainid;
  
  public String ucscname;
  public String ensemblname;
  public String taxid;
  public String organism;
  public String speciesname;
  
  public String assemblytype;
  public String assemblyclass;
  
  
  public String assemblystatus;
  public String wgs;
  public List<Object> gb_bioprojects;
  public List<Object> gb_projects;
  public List<Object> rs_bioprojects;
  public List<Object> rs_projects;
  
  public String biosampleaccn;
  public String biosampleid;
  public Object biosource;
  public String partialgenomerepresentation;
  public Object coverage;
  
  public String ftppath_genbank;
  public String ftppath_refseq;
  public String ftppath_assembly_rpt;
  public String ftppath_stats_rpt;
  public String ftppath_regions_rpt;
  public String submitterorganization;
  public String sortorder;
  public Map<String, String> synonym = new HashMap<>();
  
  public String contign50;
  public String scaffoldn50;
  
  public String refseq_category;
  
  public List<Object> anomalouslist;
  public List<Object> exclfromrefseq;
  public String primary;
  public String assemblydescription;
  public String releaselevel;
  public String releasetype;
  
  public String speciestaxid;
  public String asmreleasedate_genbank;
  public String asmreleasedate_refseq;
  public String submissiondate;
  public String lastupdatedate;
  public String asmupdatedate;
  public String seqreleasedate;
  
  public String fromtype;
  public List<String> propertylist;
  public String meta;
  
  public Object assemblystatussort;
}
