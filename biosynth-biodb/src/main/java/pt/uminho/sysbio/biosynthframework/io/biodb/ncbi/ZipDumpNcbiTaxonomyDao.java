package pt.uminho.sysbio.biosynthframework.io.biodb.ncbi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.ZipContainer;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class ZipDumpNcbiTaxonomyDao {

  private static final Logger logger = LoggerFactory.getLogger(ZipDumpNcbiTaxonomyDao.class);

  //public static Map<String, Map<String, String>> data = new HashMap<>();
  public Map<Long, Long> entryToParentEntry = null;
  public Map<Long, Long> mergeMap = null;
  public Map<String, Map<String, String>> data = null;
  public String version;
  public File dump;
  
  public ZipDumpNcbiTaxonomyDao(File dump, String version) {
    this.dump = dump;
    this.version = version;
  }
  
  public Set<?> getAllNcbiTaxonomyEntries() {
    if (data == null) {
      load(dump.getAbsolutePath());
    }
    return null;
  }
  
  public Object getNcbiTaxonomyByEntry(String entry) {
    if (data == null) {
      load(dump.getAbsolutePath());
    }
    return null;
  }

  public void load(String dumpZip) {
    entryToParentEntry = new HashMap<>();
    mergeMap = new HashMap<>();
    data = new HashMap<>();
    //  Map<String, String> entryToParentEntry = new HashMap<>();
    ZipContainer container = null;
    InputStream nodesInputStream = null;
    InputStream namesInputStream = null;
    InputStream mergeInputStream = null;
    try {
      container = new ZipContainer(dumpZip);

      ZipRecord nodes = null;
      ZipRecord names = null;
      ZipRecord merge = null;
      for (ZipRecord zipr : container.getInputStreams()) {
        System.out.println(zipr);
        if (zipr.name.equals("nodes.dmp")) {
          logger.info("found nodes dump!");
          nodes = zipr;
        }
        if (zipr.name.equals("names.dmp")) {
          logger.info("found names dump!");
          names = zipr;
        }

        if (zipr.name.equals("merged.dmp")) {
          logger.info("found merged.dmp!");
          merge = zipr;
        }
      }

      nodesInputStream = nodes.is;
      namesInputStream = names.is;
      mergeInputStream = merge.is;

      logger.info("read nodes...");
      for (String l : IOUtils.readLines(nodesInputStream, Charset.defaultCharset())) {
        String[] p = l.split("\t");
        String tax_id = p[0];
        String parent_id = p[2];
        //      String entry = String.format("txid%s", tax_id);
        //      String parent = String.format("txid%s", parent_id);
        if (entryToParentEntry.put(Long.parseLong(tax_id), 
            Long.parseLong(parent_id)) != null) {
          logger.error("DUPLICATE KEY !CRY ! {}", tax_id);
        }
      }

      logger.info("read names...");
      for (String l : IOUtils.readLines(namesInputStream, Charset.defaultCharset())) {
        String[] p = l.split("\t");
        String field = p[6];
        String value = p[2];
        String tax_id = p[0];
        String entry = String.format("txid%s", tax_id);
        if (!data.containsKey(entry)) {
          data.put(entry, new HashMap<String, String>());
        }
        data.get(entry).put(field, value);
      }

      logger.info("read merges...");
      for (String l : IOUtils.readLines(mergeInputStream, Charset.defaultCharset())) {
        String[] p = l.split("\t");
        String from = p[0];
        String to = p[2];
        mergeMap.put(Long.parseLong(from), Long.parseLong(to));
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(nodesInputStream);
      IOUtils.closeQuietly(namesInputStream);
      IOUtils.closeQuietly(mergeInputStream);
      IOUtils.closeQuietly(container);
    }
    
  }
}
