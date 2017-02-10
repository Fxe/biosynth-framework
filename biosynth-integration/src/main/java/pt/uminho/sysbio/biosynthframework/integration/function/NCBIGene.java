package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezGene;
import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;

public class NCBIGene implements Function<EntrezGene, Map<String, Object>>{

  public Function<EntrezGene, Boolean> validator = null;
  public Function<Map<String, Object>, Boolean> propertiesValidador = null;
  
  public Map<String, String> mapping = new HashMap<> ();
  public Map<String, Function<Object, Object>> transform = new HashMap<> ();
  
  //{Gene-track={
  //Gene-track_geneid=3711490, 
  //Gene-track_create-date={
  //  Date={
  //    Date_std={
  //      Date-std={
  //        Date-std_year=2005, 
  //        Date-std_month=10, 
  //        Date-std_day=8, 
  //        Date-std_hour=1, 
  //        Date-std_minute=36, 
  //        Date-std_second=0}
  public static long getCreateDate(EntrezGene g) {
    String year = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_year");
    String month = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_month");
    String day = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_day");
    String hrs = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_hour");
    String min = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_minute");
    String sec = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", "Gene-track_create-date", "Date", 
        "Date_std", "Date-std", "Date-std_second");
    int yyyy = Integer.parseInt(year);
    //1 - Jan => 0 - Jan
    int MM = Integer.parseInt(month) - 1;
    int dd = Integer.parseInt(day);
    int HH = Integer.parseInt(hrs);
    int mm = Integer.parseInt(min);
    int ss = Integer.parseInt(sec);
    //Date date = new Date();
    GregorianCalendar calendar = new GregorianCalendar(yyyy, MM, dd, HH, mm, ss);
    long m = calendar.getTimeInMillis();
    return m;
  }

  public static long getUpdateDate(EntrezGene g) {
    final String field = "Gene-track_update-date";
    String year = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_year");
    String month = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_month");
    String day = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_day");
    String hrs = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_hour");
    String min = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_minute");
    String sec = JsonMapUtils.getString(g.Entrezgene_track_info, 
        "Gene-track", field, "Date", "Date_std", "Date-std", "Date-std_second");
    int yyyy = Integer.parseInt(year);
    //1 - Jan => 0 - Jan
    int MM = Integer.parseInt(month) - 1;
    int dd = Integer.parseInt(day);
    int HH = Integer.parseInt(hrs);
    int mm = Integer.parseInt(min);
    int ss = Integer.parseInt(sec);
    //Date date = new Date();
    GregorianCalendar calendar = new GregorianCalendar(yyyy, MM, dd, HH, mm, ss);
    long m = calendar.getTimeInMillis();
    return m;
  }

  public static String getTaxname(EntrezGene g) {

    Map<String, Object> BioSource = JsonMapUtils.getMap("BioSource", g.Entrezgene_source);
    if (BioSource == null || !BioSource.containsKey("BioSource_org")) {
      return null;
    }
    Map<String, Object> BioSource_org = JsonMapUtils.getMap("BioSource_org", BioSource);
    if (BioSource_org == null || !BioSource_org.containsKey("Org-ref")) {
      return null;
    }
    Map<String, Object> ref = JsonMapUtils.getMap("Org-ref", BioSource_org);

    return ref.get("Org-ref_taxname").toString();
  }


  //{BioSource={
  //BioSource_genome={value=chromosome, =21}, 
  //BioSource_org={
  //  Org-ref={
  //    Org-ref_taxname=Pseudoalteromonas haloplanktis TAC125, 
  //    Org-ref_db={
  //      Dbtag={
  //        Dbtag_db=taxon, 
  //        Dbtag_tag={
  //          Object-id={Object-id_id=326442}}}}
  public static Long getTxid(EntrezGene g) {
    String result = JsonMapUtils.getString(g.Entrezgene_source, 
        "BioSource", "BioSource_org", "Org-ref", "Org-ref_db", 
        "Dbtag", "Dbtag_tag", "Object-id", "Object-id_id");

    if (result != null) {
      return Long.parseLong(result);
    }

    return null;
  }

  //public static String getStart(EntrezGene g) {
  //Map<String, Object> data = g.
  //for (Map<String, Object> db : g.Entrezgene_non_unique_keys) {
  //if (db != null && db.containsKey("Dbtag_db") && 
  //    db.containsKey("Dbtag_tag") && db.get("Dbtag_db").equals("locus tag")) {
  //  oldLocus = JsonMapUtils.getString(db, "Dbtag_tag", "Object-id", "Object-id_id");
  //}
  //}
  //return oldLocus;
  //}

  public static long getGeneId(EntrezGene g) {
    Map<String, Object> Gene_track = JsonMapUtils.getMap("Gene-track", g.Entrezgene_track_info);
    Object v = Gene_track.get("Gene-track_geneid");

    long geneId;
    if (v instanceof String) {
      geneId = Long.parseLong(v.toString());
    } else {
      geneId = (long) v;
    }

    return geneId;
  }

  public static String getOldLocus(EntrezGene g) {
    String oldLocus = null;
    for (Map<String, Object> db : g.Entrezgene_non_unique_keys) {
      if (db != null && db.containsKey("Dbtag_db") && 
          db.containsKey("Dbtag_tag") && db.get("Dbtag_db").equals("locus tag")) {
        oldLocus = JsonMapUtils.getString(db, "Dbtag_tag", "Object-id", "Object-id_str");
      }
    }
    return oldLocus;
  }

  public static String getLocus(EntrezGene g) {
    return JsonMapUtils.getString(g.Entrezgene_gene, 
        "Gene-ref", "Gene-ref_locus-tag");
  }

  public static String getAccession(EntrezGene g) {
    String result = null;
    for (Map<String, Object> data : g.Entrezgene_locus) {
      String type = JsonMapUtils.getString(data, "Gene-commentary_type", "value");
      if (type.equals("genomic")) {
        result = JsonMapUtils.getString(data, "Gene-commentary_accession");
      }
    }
    return result;
  }
  //Gene-commentary_seqs={
  //Seq-loc={
  //  Seq-loc_int={
  //    Seq-interval={
  //      Seq-interval_from=95740, 
  //      Seq-interval_to=96483, 
  //      Seq-interval_strand={
  //        Na-strand={
  //          value=plus}}, 
  public static Long getStart(EntrezGene g) {
    String result = null;
    for (Map<String, Object> data : g.Entrezgene_locus) {
      String type = JsonMapUtils.getString(data, "Gene-commentary_type", "value");
      if (type.equals("genomic")) {
        result = JsonMapUtils.getString(data, 
            "Gene-commentary_seqs", "Seq-loc", "Seq-loc_int", "Seq-interval", 
            "Seq-interval_from");
      }
    }

    if (result != null) {
      return Long.parseLong(result);
    }

    return null;
  }

  public static Long getEnd(EntrezGene g) {
    String result = null;
    for (Map<String, Object> data : g.Entrezgene_locus) {
      String type = JsonMapUtils.getString(data, "Gene-commentary_type", "value");
      if (type.equals("genomic")) {
        result = JsonMapUtils.getString(data, 
            "Gene-commentary_seqs", "Seq-loc", "Seq-loc_int", "Seq-interval", 
            "Seq-interval_to");
      }
    }

    if (result != null) {
      return Long.parseLong(result);
    }

    return null;
  }

  public static String getStrand(EntrezGene g) {
    String result = null;
    for (Map<String, Object> data : g.Entrezgene_locus) {
      String type = JsonMapUtils.getString(data, "Gene-commentary_type", "value");
      if (type.equals("genomic")) {
        result =  JsonMapUtils.getString(data, 
            "Gene-commentary_seqs", "Seq-loc", "Seq-loc_int", "Seq-interval", 
            "Seq-interval_strand", "Na-strand", "value");
      }
    }
    return result;
  }
  //BioSource_subtype={
  //SubSource={
  //  SubSource_subtype={value=chromosome, =1}, 
  //  SubSource_name=II}}}}
  public static Pair<String, String> getSubtype(EntrezGene g) {
    String type = null;
    String value = null;
    if (g.Entrezgene_source.containsKey("BioSource")) {
      @SuppressWarnings("unchecked")
      Map<String, Object> data = 
      (Map<String, Object> )g.Entrezgene_source.get("BioSource");
      if (data.containsKey("BioSource_subtype")) {
        type = JsonMapUtils.getString(data, 
            "BioSource_subtype", "SubSource", "SubSource_subtype", "value");
        value = JsonMapUtils.getString(data, 
            "BioSource_subtype", "SubSource", "SubSource_name");
      }
    }

    return new ImmutablePair<>(type, value);
  }
  
  public Map<String, Object> getProperties(EntrezGene g) {
    long geneId = getGeneId(g);
    long create_date = getCreateDate(g);
    long update_date = getUpdateDate(g);
    Pair<String, String> genomeSource = getSubtype(g);
//    String taxname = getTaxname(g);
    String locus = getLocus(g);
    String oldLocus = getOldLocus(g);
    String accession = getAccession(g);
    long txId = getTxid(g);
    Long start = getStart(g);
    Long end = getEnd(g);
    String strand = getStrand(g);
    
    Map<String, Object> properties = new HashMap<> ();
    properties.put("CreateDate", create_date);
    properties.put("UpdateDate", update_date);

    properties.put("genome_source_type", genomeSource.getLeft());
    properties.put("genome_source_value", genomeSource.getRight());
    
    properties.put("taxa_id", txId);
    properties.put("gene_id", geneId);
    properties.put("accession", accession);
    properties.put("locus", locus);
    properties.put("old_locus", oldLocus);
    properties.put("seq_start", start);
    properties.put("seq_end", end);
    properties.put("strand", strand);
    return properties;
  }
  
  public Map<String, Object> mapProperties(Map<String, Object> properties) {
    Map<String, Object> result = new HashMap<> ();
    for (String k : properties.keySet()) {
      if (mapping.containsKey(k)) {
        String k_ = mapping.get(k);
        Object v_ = properties.get(k);
        if (transform.containsKey(k)) {
          v_ = transform.get(k).apply(v_);
        }
        
        if (v_ != null) {
          result.put(k_, v_);
        }
        
      } else {
        throw new RuntimeException("found unknown field: " + k);
      }
    }
    
    String entry = String.format("uid%d", properties.get("gene_id"));
    result.put(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    result.put(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, GlobalLabel.NCBIGene.toString());
    result.put(Neo4jDefinitions.PROXY_PROPERTY, false);
    return result;
  }
  
  @Override
  public Map<String, Object> apply(EntrezGene gene) {
    
    if (validator != null && validator.apply(gene) == false) {
      return null;
    }
    
    Map<String, Object> properties = getProperties(gene);
    Map<String, Object> mproperties = mapProperties(properties);
    
    if (propertiesValidador != null && propertiesValidador.apply(mproperties) == false) {
      return null;
    }
    
    return mproperties;
  }

}
