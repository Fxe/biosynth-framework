package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.biodb.ymdb.YmdbMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.ymdb.YmdbMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class ZipYmdbMetaboliteDao implements MetaboliteDao<YmdbMetaboliteEntity> {
  public Map<String, YmdbMetabolite> data = new HashMap<>();
  public Function<YmdbMetabolite, YmdbMetaboliteEntity> transform = new Transform();
  
  public static class Transform implements Function<YmdbMetabolite, YmdbMetaboliteEntity> {

    @Override
    public YmdbMetaboliteEntity apply(YmdbMetabolite r) {
      YmdbMetaboliteEntity e = new YmdbMetaboliteEntity();
      e.setEntry(r.ymdb_id);
      return e;
    }
    
  }
  
  public ZipYmdbMetaboliteDao(File zip) {
    try {
      ZipContainer container = new ZipContainer(zip.getAbsolutePath());
      for (ZipRecord record : container.getInputStreams()) {
        if (record.name.equals("ymdb.json")) {
          ObjectMapper om = new ObjectMapper();
          JavaType jtype = om.getTypeFactory().constructCollectionType(List.class, YmdbMetabolite.class);
          List<YmdbMetabolite> cpdList = om.readValue(record.is, jtype);
          for (YmdbMetabolite cpd : cpdList) {
            data.put(cpd.ymdb_id, cpd);
          }
          record.is.close();
        }
      }
      container.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  
  
  @Override
  public List<String> getAllMetaboliteEntries() {
    return new ArrayList<> (this.data.keySet());
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public YmdbMetaboliteEntity getMetaboliteByEntry(String entry) {
    YmdbMetaboliteEntity cpd = null;
    if (this.data.containsKey(entry)) {
      cpd = this.transform.apply(this.data.get(entry));
    }
    
    return cpd;
  }

  @Override
  public YmdbMetaboliteEntity getMetaboliteById(Serializable arg0) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable save(YmdbMetaboliteEntity arg0) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public YmdbMetaboliteEntity saveMetabolite(YmdbMetaboliteEntity arg0) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable saveMetabolite(Object arg0) {
    throw new RuntimeException("Operation not supported");
  }
}
