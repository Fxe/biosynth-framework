package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

/**
 * 
 * @author Filipe Liu
 *
 */

@Repository
public class CsvBiggMetaboliteDaoImpl implements MetaboliteDao<BiggMetaboliteEntity>{

  private static final Logger logger = LoggerFactory.getLogger(CsvBiggMetaboliteDaoImpl.class);

  private Resource csvFile;

  public Resource getCsvFile() { return csvFile;}
  public void setCsvFile(Resource csvFile) { this.csvFile = csvFile;}

  private Map<Long, String> idToEntry = new HashMap<> ();
  private Map<String, BiggMetaboliteEntity> cachedData = new HashMap<> ();

  @Override
  public Serializable save(BiggMetaboliteEntity entity) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    if (idToEntry.isEmpty()) {
      this.initialize();
    }

    return new ArrayList<Serializable> (this.idToEntry.keySet());
  }

  @Override
  public BiggMetaboliteEntity getMetaboliteById(Serializable id) {
    if (idToEntry.isEmpty()) {
      this.initialize();
    }

    if (!this.idToEntry.containsKey(id)) return null;
    String entry = this.idToEntry.get(id);
    return this.cachedData.get(entry);
  }

  @Override
  public BiggMetaboliteEntity saveMetabolite(
      BiggMetaboliteEntity metabolite) {
    throw new RuntimeException("Not Supported Operation");
  }
  @Override
  public Serializable saveMetabolite(Object entity) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public BiggMetaboliteEntity getMetaboliteByEntry(String entry) {
    if (idToEntry.isEmpty()) {
      this.initialize();
    }

    if (!this.cachedData.containsKey(entry)) return null;

    return this.cachedData.get(entry);
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    if (!idToEntry.isEmpty()) {
      return new ArrayList<String> (this.idToEntry.values());
    }

    this.initialize();

    return new ArrayList<String> (this.idToEntry.values());
  }

  public void initialize() {
    this.cachedData.clear();
    this.idToEntry.clear();

    try {
      List<BiggMetaboliteEntity> res = new ArrayList<> ();
      InputStream in = csvFile.getInputStream();
      res = DefaultBiggMetaboliteParser.parseMetabolites(in);

      for (BiggMetaboliteEntity cpd : res) {
        this.cachedData.put(cpd.getEntry(), cpd);
        this.idToEntry.put(cpd.getInternalId(), cpd.getEntry());
      }

      in.close();
    } catch (IOException e) {
      logger.error(String.format("IOException - [%s]", e.getMessage()));
    }
  }



  //	@Deprecated
  //	public BiggMetaboliteEntity find(Serializable id) {
  //		for (BiggMetaboliteEntity c : this.findAll()) {
  //			if (c.getEntry().equals(id)) return c;
  //		}
  //		return null;
  //	}
  //
  //	@Deprecated
  //	public List<BiggMetaboliteEntity> findAll() {
  //		this.cachedData.clear();
  //		this.idToEntry.clear();
  //		
  //		List<BiggMetaboliteEntity> cpdList = null;
  //
  //		cpdList = null; //DefaultBiggMetaboliteParser.parseMetabolites(csvFile);
  //		
  //		for (BiggMetaboliteEntity cpd: cpdList) {
  //			this.cachedData.put(cpd.getEntry(), cpd);
  //			this.idToEntry.put(cpd.getId(), cpd.getEntry());
  //		}
  //		return cpdList;
  //	}
}
